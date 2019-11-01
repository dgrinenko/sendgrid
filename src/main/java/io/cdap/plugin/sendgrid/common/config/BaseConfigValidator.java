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
package io.cdap.plugin.sendgrid.common.config;

import com.google.common.base.Strings;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.cdap.etl.api.validation.ValidationFailure;
import io.cdap.plugin.sendgrid.common.SendGridClient;
import io.cdap.plugin.sendgrid.common.objects.SendGridAuthType;

import java.io.IOException;

/**
 * Validates configuration
 */
public abstract class BaseConfigValidator {
  protected FailureCollector failureCollector;
  protected SendGridClient client = null;
  private BaseConfig config;

  public BaseConfigValidator(FailureCollector failureCollector, BaseConfig config) {
    this.failureCollector = failureCollector;
    this.config = config;
  }

  private void checkAuthType() {
    try {
      config.getAuthType();
    } catch (IllegalArgumentException e) {
      failureCollector.addFailure(String.format("Wrong authentication method selected: %s", e.getMessage()), null)
        .withConfigProperty(BaseConfig.PROPERTY_AUTH_TYPE);
    }
  }

  private void checkAuthData() {
    boolean tryToLogin = true;

    switch (config.getAuthType()) {
      case BASIC:
        if (Strings.isNullOrEmpty(config.getAuthUserName())) {
          failureCollector.addFailure("User name is not set", null)
            .withConfigProperty(BaseConfig.PROPERTY_AUTH_USERNAME);
          tryToLogin = false;
        }
        if (Strings.isNullOrEmpty(config.getAuthPassword())) {
          failureCollector.addFailure("Password is not set", null)
            .withConfigProperty(BaseConfig.PROPERTY_AUTH_PASSWORD);
          tryToLogin = false;
        }

        if (tryToLogin) {
          client = new SendGridClient(config.getAuthUserName(), config.getAuthPassword());
        }
        break;
      case API:
        if (Strings.isNullOrEmpty(config.getSendGridApiKey())) {
          failureCollector.addFailure("API Key is not set", null)
            .withConfigProperty(BaseConfig.PROPERTY_SENDGRID_API_KEY);
          tryToLogin = false;
        }

        if (tryToLogin) {
          client = new SendGridClient(config.getSendGridApiKey());
        }
        break;
    }
  }

  private void checkClientConnectivity() {
    try {
      client.checkConnection();
    } catch (IOException e) {
      ValidationFailure failure = failureCollector
        .addFailure(String.format("Connectivity issues: %s", e.getMessage()), null)
        .withStacktrace(e.getStackTrace());

      if (config.getAuthType() == SendGridAuthType.BASIC) {
        failure
          .withConfigProperty(BaseConfig.PROPERTY_AUTH_USERNAME)
          .withConfigProperty(BaseConfig.PROPERTY_AUTH_PASSWORD);
      }
      if (config.getAuthType() == SendGridAuthType.API) {
        failure.withConfigProperty(BaseConfig.PROPERTY_SENDGRID_API_KEY);
      }
    }
  }

  /**
   * Perform validation tasks which did not involve API Client usage
   */
  public abstract void doValidation();

  public void validate() {
    client = null;

    if (!config.containsMacro(BaseConfig.PROPERTY_AUTH_TYPE)) {
      checkAuthType();
    }
    if (!config.containsMacro(BaseConfig.PROPERTY_AUTH_USERNAME) &&
        !config.containsMacro(BaseConfig.PROPERTY_AUTH_PASSWORD) &&
        !config.containsMacro(BaseConfig.PROPERTY_SENDGRID_API_KEY)) {
      checkAuthData();
    }

    doValidation();

    // client could be not constructed, if any of checkAuth tests failed
    if (client != null
        && (!config.containsMacro(BaseConfig.PROPERTY_AUTH_USERNAME) &&
            !config.containsMacro(BaseConfig.PROPERTY_AUTH_PASSWORD) &&
            !config.containsMacro(BaseConfig.PROPERTY_SENDGRID_API_KEY)
        )) {
      checkClientConnectivity();
    }
  }
}
