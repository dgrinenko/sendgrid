/*
 * Copyright © 2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cdap.plugin.sendgrid.common;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import io.cdap.plugin.sendgrid.common.config.BaseConfig;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.helpers.ObjectInfo;
import io.cdap.plugin.sendgrid.common.objects.BasicResult;
import io.cdap.plugin.sendgrid.common.objects.SendGridAuthType;
import io.cdap.plugin.sendgrid.common.objects.mail.SendGridMail;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * SendGrid Client
 */
public class SendGridClient {
  private static final String CONNECTION_CHECK_ENDPOINT = "alerts";

  /**
   * Extended version of the original SendGrid API wrapper with added support of basic auth
   */
  private static class SendGridAPIClient extends SendGrid {

    SendGridAPIClient(String apiKey) {
      super(apiKey);
    }

    SendGridAPIClient(String username, String password) {
      super("");  // actual key is not required due to it would be rewritten with basic auth data
      initializeSendGrid(username, password);
    }

    /**
     * Replaces "Authorization" header, configured for the bearer auth with basic auth credentials
     *
     * @param username name of the user
     * @param password password of the user
     */
    private void initializeSendGrid(String username, String password) {
      String encoding = Base64.getEncoder().encodeToString((String.format("%s:%s", username, password).getBytes()));
      addRequestHeader("Authorization", String.format("Basic %s", encoding));
    }
  }

  private SendGridAPIClient sendGrid;
  private Gson gson;

  private SendGridClient() {
    gson = new GsonBuilder().create();
  }

  public SendGridClient(BaseConfig config) {
    this();
    if (config.getAuthType() == SendGridAuthType.API) {
      sendGrid = new SendGridAPIClient(config.getSendGridApiKey());
    } else if (config.getAuthType() == SendGridAuthType.BASIC) {
      sendGrid = new SendGridAPIClient(config.getAuthUserName(), config.getAuthPassword());
    } else {
      throw new IllegalArgumentException(String.format("Invalid authentication method '%s'",
        SendGridClient.class.getCanonicalName()));
    }
  }

  public SendGridClient(String key) {
    this();
    sendGrid = new SendGridAPIClient(key);
  }

  public SendGridClient(String username, String password) {
    this();
    sendGrid = new SendGridAPIClient(username, password);
  }

  /**
   * Low level function to query API endpoints
   *
   * @param method the HTTP method to use
   * @param endpoint relative uri to be queried
   * @param arguments arguments for the query
   * @param data
   * @return query body
   * @throws IOException if any issue with query the API happen
   */
  private String makeApiRequest(Method method, String endpoint, @Nullable Map<String, String> arguments,
                                String data) throws IOException {

    Request request = new Request();
    request.setMethod(method);
    request.setEndpoint(endpoint);

    if (method == Method.POST && !Strings.isNullOrEmpty(data)) {
      request.setBody(data);
    }

    if (arguments != null && !arguments.isEmpty()) {
      arguments.forEach(request::addQueryParam);
    }

    Response response;
    try {
      response = sendGrid.api(request);
    } catch (IOException e) {
      String serverMessage = String.format("Request to SendGrid API \"%s\"", endpoint);
      // Parse error response from the server
      if (e.getMessage().contains("Body:")) {
        String[] messages = e.getMessage().split("Body:");
        HashMap<String, List<HashMap<String, String>>> errors = gson.fromJson(
          messages[1],
          new TypeToken<HashMap<String, List<HashMap<String, String>>>>() { }.getType()
        );
        if (errors.containsKey("errors")) {
          String description = errors.get("errors").stream()
            .filter(x -> x.containsKey("message"))
            .map(x -> x.get("message"))
            .collect(Collectors.joining(";"));

          serverMessage = String.format("%s, API response: %s", messages[0], description);
        }
      }
      throw new IOException(serverMessage, e);
    }

    return response.getBody();
  }

  /**
   * Checks connection to the service by testing API endpoint, in case
   * of exception would be generated {@link IOException}
   */
  public void checkConnection() throws IOException {
      makeApiRequest(Method.GET, CONNECTION_CHECK_ENDPOINT, null, null);
  }

  /**
   * Verify all incoming arguments for the query object
   *
   * @param objectInfo objects definition
   * @param arguments query arguments
   * @throws IllegalArgumentException if any validation issue
   */
  private void checkIncomingArguments(ObjectInfo objectInfo, Map<String, String> arguments)
    throws IllegalArgumentException {

    if (objectInfo.getRequiredArguments() != null && !objectInfo.getRequiredArguments().isEmpty()) {
      if (arguments == null || arguments.isEmpty()) {
        throw new IllegalArgumentException(String.format(
          "Object '%s' require input arguments to be passed, nothing found",
          objectInfo.getCdapObjectName()
        ));
      }
      List<String> exceptions = new ArrayList<>();

      objectInfo.getRequiredArguments().forEach(x -> {
        try {
          if (Strings.isNullOrEmpty(x)) {
            return;
          }

          arguments.keySet().stream()
            .filter(x::equals)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format(
              "Object '%s' require %s argument, but nothing provided",
              objectInfo.getCdapObjectName(),
              x
            )));
        } catch (IllegalArgumentException e) {
          exceptions.add(e.getMessage());
        }
      });
      if (!exceptions.isEmpty()) {
        throw new IllegalArgumentException(exceptions.stream().collect(Collectors.joining(System.lineSeparator())));
      }
    }
  }

  /**
   * Post a SendGrid mail
   *
   * @param mail Mail object to send
   */
  public void  sendMail(SendGridMail mail) throws IOException {
     ObjectInfo objectInfo = ObjectHelper.getObjectInfo(SendGridMail.class);
     String data = gson.toJson(mail);

     makeApiRequest(Method.POST, objectInfo.getSendGridAPIUrl(), null, data);
  }

  /**
   * Query SendGrid API using plugin meta objects
   *
   * @param objectInfo objects definition
   * @param arguments query arguments
   * @return object representation of the query
   * @throws IOException if any issue with query the API happen
   * @throws IllegalStateException unsupported response type caught
   * @throws IllegalArgumentException if any validation issue
   */
  public List<IBaseObject> getObject(ObjectInfo objectInfo, Map<String, String> arguments)
    throws IOException, IllegalStateException, IllegalArgumentException {
    checkIncomingArguments(objectInfo, arguments);

    String endpoint = objectInfo.getSendGridAPIUrl();
    String response = makeApiRequest(Method.GET, endpoint, arguments, null);
    Class clazz = objectInfo.getObjectClass();

    if (objectInfo.getApiResponseType() == APIResponseType.RESULT) {
      Type typeToken = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
          return new Type[] { clazz };
        }

        @Override
        public Type getRawType() {
          return BasicResult.class;
        }

        @Override
        public Type getOwnerType() {
          return null;
        }
      };
      BasicResult<IBaseObject> result = gson.fromJson(response, typeToken);

      return result.getResult();
    } else if (objectInfo.getApiResponseType() == APIResponseType.LIST) {
      Type typeToken = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
          return new Type[] { clazz };
        }

        @Override
        public Type getRawType() {
          return List.class;
        }

        @Override
        public Type getOwnerType() {
          return null;
        }
      };

      return gson.fromJson(response, typeToken);
    } else {
      throw new IllegalStateException(String.format(
        "Unsupported API Response type: %s",
        objectInfo.getApiResponseType().name()
      ));
    }
  }

  /**
   * Query SendGrid API using plugin meta objects
   *
   * @param objectInfo objects definition
   * @return object representation of the query
   * @throws IOException if any issue with query the API happen
   * @throws IllegalStateException unsupported response type caught
   * @throws IllegalArgumentException if any validation issue
   */
  public List<IBaseObject> getObject(ObjectInfo objectInfo)
    throws IOException, IllegalStateException, IllegalArgumentException {

    return getObject(objectInfo, null);
  }
}
