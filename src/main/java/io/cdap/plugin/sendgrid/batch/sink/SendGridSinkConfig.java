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
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.sendgrid.common.config.BaseConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * SendGrid Sink Plugin configuration
 */
public class SendGridSinkConfig extends BaseConfig {
  public static final String PROPERTY_RECIPIENT_ADDRESS_SOURCE = "recipientAddressSource";
  public static final String PROPERTY_RECIPIENT_CONFIG_ADDRESS = "recipientConfigAddressList";
  public static final String PROPERTY_RECIPIENT_COLUMN = "recipientColumnName";
  public static final String PROPERTY_BODY_COLUMN = "bodyColumnName";
  public static final String PROPERTY_FROM = "from";
  public static final String PROPERTY_REPLY_TO = "replyTo";
  public static final String PROPERTY_FOOTER_ENABLED = "footerEnabled";
  public static final String PROPERTY_FOOTER_HTML = "footerHtml";
  public static final String PROPERTY_SANDBOX_MODE = "sandboxMode";
  public static final String PROPERTY_CLICK_TRACKING = "clickTracking";
  public static final String PROPERTY_OPEN_TRACKING = "openTracking";
  public static final String PROPERTY_SUBSCRIPTION_TRACKING = "subscriptionTracking";
  public static final String PROPERTY_MAIL_SUBJECT = "mailSubject";

  public static final String TO_TYPE_INPUT = "input";
  public static final String TO_TYPE_CONFIG = "config";

  public String getMailSubject() {
    return mailSubject;
  }

  /**
   * Available sources for recipient addresses
   */
  public enum ToAddressSource {
    CONFIG,
    INPUT;

    public static ToAddressSource fromString(String toType) {
      switch (toType) {
        case TO_TYPE_INPUT:
          return ToAddressSource.INPUT;
        case TO_TYPE_CONFIG:
          return ToAddressSource.CONFIG;
        default:
          throw new IllegalArgumentException(String.format("Unknown address source '%s', allowed: '%s', '%s'",
              toType, TO_TYPE_INPUT, TO_TYPE_CONFIG));
      }
    }
  }

  @Name(PROPERTY_FROM)
  @Description("The author of the message")
  @Macro
  private String from;

  @Name(PROPERTY_REPLY_TO)
  @Description("Email address to which the author of the message suggests that replies be sent")
  @Nullable
  @Macro
  private String replyTo;

  @Name(PROPERTY_FOOTER_ENABLED)
  @Description("Footer feature setting switcher")
  @Nullable
  @Macro
  private String footerEnable;

  @Name(PROPERTY_FOOTER_HTML)
  @Description("The default footer which would be included to every email")
  @Nullable
  @Macro
  private String footerHTML;

  @Name(PROPERTY_SANDBOX_MODE)
  @Description("Allows to send a test email to ensure that your request body is valid and formatted correctly")
  @Nullable
  @Macro
  private String sandboxMode;

  @Name(PROPERTY_CLICK_TRACKING)
  @Description("Allows to track whether a recipient clicked a link in a email")
  @Nullable
  @Macro
  private String clickTracking;

  @Name(PROPERTY_OPEN_TRACKING)
  @Description("Allows to track whether the email was opened or not, by including a single pixel image in the" +
      " body of the content. When the pixel is loaded, SendGrid can log that the email was opened")
  @Nullable
  @Macro
  private String openTracking;

  @Name(PROPERTY_SUBSCRIPTION_TRACKING)
  @Description("Allows to insert a subscription management link at the bottom of the text and html" +
      " bodies of an email")
  @Nullable
  @Macro
  private String subscriptionTracking;

  @Name(PROPERTY_RECIPIENT_ADDRESS_SOURCE)
  @Description("Recipients addresses source selection")
  @Macro
  private String recipientAddressSource;

  @Name(PROPERTY_RECIPIENT_CONFIG_ADDRESS)
  @Description("List of mail recipients")
  @Nullable
  @Macro
  private String recipientConfigAddressList;

  @Name(PROPERTY_RECIPIENT_COLUMN)
  @Description("Name of the column with coma-separated list of recipients")
  @Nullable
  @Macro
  private String recipientColumnName;

  @Name(PROPERTY_BODY_COLUMN)
  @Description("Name of the column for the mail content")
  @Macro
  private String bodyColumnName;

  @Name(PROPERTY_MAIL_SUBJECT)
  @Description("Email Subject")
  @Macro
  private String mailSubject;

  /**
   * Constructor
   *
   * @param referenceName uniquely identify source/sink for lineage, annotating metadata, etc.
   */
  public SendGridSinkConfig(String referenceName) {
    super(referenceName);
  }

  public void validate(FailureCollector failureCollector) {
    new SendGridSinkConfigValidator(failureCollector, this).validate();
  }

  public void validate(Schema schema) {
    if (schema == null) {
      throw new IllegalArgumentException("Input schema cannot be empty");
    }

    if (getRecipientAddressSource() == ToAddressSource.INPUT) {
      Schema.Field recipient = schema.getField(recipientColumnName);
      if (recipient == null) {
        throw new IllegalArgumentException(String.format("Plugin is configured to use column '%s' for" +
          " recipient addresses, but input schema did not provide such column", recipientColumnName));
      }

      if (recipient.getSchema().getType() != Schema.Type.STRING) {
        throw new IllegalArgumentException(String.format("The input schema column '%s' expected to be of type STRING",
          recipientColumnName));
      }
    }

    Schema.Field body = schema.getField(bodyColumnName);
    if (body == null) {
      throw new IllegalArgumentException(String.format("Plugin requires column '%s' for" +
        " recipient addresses, but input schema did not provide such column", recipientColumnName));
    }
    if (body.getSchema().getType() != Schema.Type.STRING) {
      throw new IllegalArgumentException(String.format("The input schema column '%s' expected to be of type STRING",
        bodyColumnName));
    }
  }

  public String getFrom() {
    if (Strings.isNullOrEmpty(from)) {
      throw new IllegalArgumentException(String.format("Property '%s' cannot be empty", PROPERTY_FROM));
    }
    return from;
  }

  public ToAddressSource getRecipientAddressSource() {
    return ToAddressSource.fromString(recipientAddressSource);
  }

  public List<String> getRecipientAddresses() {
    if (recipientConfigAddressList == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(recipientConfigAddressList.split(","));
  }

  @Nullable
  public String getRecipientColumnName() {
    return recipientColumnName;
  }

  @Nullable
  public String getBodyColumnName() {
    return bodyColumnName;
  }

  @Nullable
  public String getReplyTo() {
    return replyTo;
  }

  public Boolean getFooterEnable() {
    return !Strings.isNullOrEmpty(footerEnable) && footerEnable.equals("true");
  }

  public String getFooterHTML() {
    return footerHTML;
  }

  public Boolean getSandboxMode() {
    return !Strings.isNullOrEmpty(sandboxMode) && sandboxMode.equals("true");
  }

  public Boolean getClickTracking() {
    return !Strings.isNullOrEmpty(clickTracking) && clickTracking.equals("true");
  }

  public Boolean getOpenTracking() {
    return !Strings.isNullOrEmpty(openTracking) && openTracking.equals("true");
  }

  public Boolean getSubscriptionTracking() {
    return !Strings.isNullOrEmpty(subscriptionTracking) && subscriptionTracking.equals("true");
  }
}
