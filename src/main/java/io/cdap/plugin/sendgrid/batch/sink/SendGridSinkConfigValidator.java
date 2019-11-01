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
package io.cdap.plugin.sendgrid.batch.sink;

import com.google.common.base.Strings;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.sendgrid.common.config.BaseConfigValidator;

import java.util.regex.Pattern;

/**
 * Sink Config Validator
 */
public class SendGridSinkConfigValidator extends BaseConfigValidator {

  private SendGridSinkConfig config;
  private static Pattern basicMailRegEx = Pattern.compile("^[\\w\\d.+\\-]+@[\\w\\d]+\\.[\\w\\d]+$");

  public SendGridSinkConfigValidator(FailureCollector failureCollector, SendGridSinkConfig config) {
    super(failureCollector, config);

    this.config = config;
  }

  /**
   * Check if provided string is a valid email
   * @param email provided email address
   */
  private boolean validateEmail(String email) {
    return basicMailRegEx.matcher(email).find();
  }

  private void checkFrom() {
    if (Strings.isNullOrEmpty(config.getFrom()) || !validateEmail(config.getFrom())) {
      failureCollector.addFailure("Not a valid or empty email address", null)
          .withConfigProperty(SendGridSinkConfig.PROPERTY_FROM);
    }
  }

  private void checkReplyTo() {
    if (!Strings.isNullOrEmpty(config.getReplyTo()) && validateEmail(config.getReplyTo())) {
      failureCollector.addFailure("Not a valid or empty email address", null)
          .withConfigProperty(SendGridSinkConfig.PROPERTY_FROM);
    }
  }

  private void checkFooter() {
    if (config.getFooterEnable() && Strings.isNullOrEmpty(config.getFooterHTML())) {
      failureCollector.addFailure("Footer content cannot be empty", null)
          .withConfigProperty(SendGridSinkConfig.PROPERTY_FOOTER_HTML);
    }
  }

  /**
   * Perform validation tasks which did not involve API Client usage
   */
  @Override
  public void doValidation() {
    if (!config.containsMacro(SendGridSinkConfig.PROPERTY_FROM)) {
      checkFrom();
    }
    if (!config.containsMacro(SendGridSinkConfig.PROPERTY_REPLY_TO)) {
      checkReplyTo();
    }
    if (!config.containsMacro(SendGridSinkConfig.PROPERTY_FOOTER_ENABLED) &&
    !config.containsMacro(SendGridSinkConfig.PROPERTY_FOOTER_HTML)) {
      checkFooter();
    }
  }
}
