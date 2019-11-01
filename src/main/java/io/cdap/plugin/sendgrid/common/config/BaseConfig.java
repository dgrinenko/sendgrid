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

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.common.ReferencePluginConfig;
import io.cdap.plugin.sendgrid.common.objects.SendGridAuthType;

import javax.annotation.Nullable;

/**
 * Provides all required configuration for reading SendGrid information
 */
public abstract class BaseConfig extends ReferencePluginConfig {
  public static final String PLUGIN_NAME = "SendGrid";

  public static final String PROPERTY_AUTH_TYPE = "authType";
  public static final String PROPERTY_SENDGRID_API_KEY = "sendGridApiKey";
  public static final String PROPERTY_AUTH_USERNAME = "username";
  public static final String PROPERTY_AUTH_PASSWORD = "password";

  @Name(PROPERTY_AUTH_TYPE)
  @Description("The way, how user would like to be authenticated to the SendGrid account")
  @Macro
  private String authType;

  @Name((PROPERTY_SENDGRID_API_KEY))
  @Description("The SendGrid API Key taken from the SendGrid account")
  @Macro
  @Nullable
  private String sendGridApiKey;

  @Name(PROPERTY_AUTH_USERNAME)
  @Description("Login name for the SendGrid account")
  @Macro
  @Nullable
  private String authUserName;

  @Name(PROPERTY_AUTH_PASSWORD)
  @Description("Password for the SendGrid account")
  @Macro
  @Nullable
  private String authPassword;

  /**
   * Constructor
   *
   * @param referenceName uniquely identify source/sink for lineage, annotating metadata, etc.
   */
  public BaseConfig(String referenceName) {
    super(referenceName);
  }

  /**
   * Validate configuration for the issues
   *
   */
  protected abstract void validate(FailureCollector failureCollector);

  /**
   * Client authentication way
   */
  public SendGridAuthType getAuthType() {
    switch (authType) {
      case "api":
        return SendGridAuthType.API;
      case "basic":
        return SendGridAuthType.BASIC;
      default:
        throw new IllegalArgumentException(String.format("Authentication using '%s' is not supported", authType));
    }
  }

  /**
   * Retrieves Api Key
   */
  @Nullable
  public String getSendGridApiKey() {
    return sendGridApiKey;
  }

  /**
   * Retrieves username
   */
  @Nullable
  public String getAuthUserName() {
    return authUserName;
  }

  /**
   * Retrieves password
   */
  @Nullable
  public String getAuthPassword() {
    return authPassword;
  }
}
