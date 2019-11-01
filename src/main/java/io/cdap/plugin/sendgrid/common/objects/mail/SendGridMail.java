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
package io.cdap.plugin.sendgrid.common.objects.mail;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;

import java.util.List;
import java.util.Map;

/**
 * SendGrid Mail
 */
@ObjectDefinition(
    APIUrl = "mail/send",
    ObjectType = ObjectDefinition.ObjectDefinitionType.CUSTOM
)
public class SendGridMail extends BaseObject implements IBaseObject {

  @SerializedName("personalizations")
  private List<SendGridPersonalizations> personalizations;

  @SerializedName("from")
  private SendGridMailPerson from;

  @SerializedName("reply_to")
  private SendGridMailPerson replyTo;

  @SerializedName("subject")
  private String subject;

  @SerializedName("content")
  private List<SendGridMailContent> content;

  @SerializedName("mail_settings")
  private SendGridMailSettings mailSettings;

  @SerializedName("tracking_settings")
  private SendGridTrackingSettings trackingSettings;

  public SendGridMail(List<SendGridPersonalizations> personalizations, SendGridMailPerson from,
                      SendGridMailPerson replyTo, String subject, List<SendGridMailContent> content,
                      SendGridMailSettings mailSettings, SendGridTrackingSettings trackingSettings) {
    this.personalizations = personalizations;
    this.from = from;
    this.replyTo  = replyTo;
    this.subject = subject;
    this.content = content;
    this.mailSettings = mailSettings;
    this.trackingSettings = trackingSettings;
  }

  SendGridMail() {
    // no-op, required for the SendGridMailBuilder
  }

  public List<SendGridPersonalizations> getPersonalizations() {
    return personalizations;
  }

  public SendGridMailPerson getFrom() {
    return from;
  }

  public SendGridMailPerson getReplyTo() {
    return replyTo;
  }

  public String getSubject() {
    return subject;
  }

  public List<SendGridMailContent> getContent() {
    return content;
  }

  public SendGridMailSettings getMailSettings() {
    return mailSettings;
  }

  public SendGridTrackingSettings getTrackingSettings() {
    return trackingSettings;
  }

  public void setPersonalizations(List<SendGridPersonalizations> personalizations) {
    this.personalizations = personalizations;
  }

  void setFrom(SendGridMailPerson from) {
    this.from = from;
  }

  void setReplyTo(SendGridMailPerson replyTo) {
    this.replyTo = replyTo;
  }

  void setSubject(String subject) {
    this.subject = subject;
  }

  void setContent(List<SendGridMailContent> content) {
    this.content = content;
  }

  void setMailSettings(SendGridMailSettings mailSettings) {
    this.mailSettings = mailSettings;
  }

  void setTrackingSettings(SendGridTrackingSettings trackingSettings) {
    this.trackingSettings = trackingSettings;
  }

  /**
   * Map of all object fields with values
   */
  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
        .put("personalization", personalizations)
        .put("from", from)
        .put("reply_to", replyTo)
        .put("subject", subject)
        .put("content", content)
        .put("mail_settings", mailSettings)
        .put("tracking_settings", trackingSettings)
        .build();
  }
}
