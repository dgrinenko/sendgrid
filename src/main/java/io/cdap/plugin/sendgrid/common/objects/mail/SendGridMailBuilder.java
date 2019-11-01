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

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 *  {@link SendGridMail} object builder
 */
public class SendGridMailBuilder {
  private SendGridMail mail;
  private SendGridPersonalizations personalizations;

  public static SendGridMailBuilder getInstance() {
    return new SendGridMailBuilder();
  }

  private SendGridMailBuilder() {
    this.mail = new SendGridMail();
  }

  private SendGridMailBuilder addContent(String type, @Nullable String content) {
    if (Strings.isNullOrEmpty(content)) {
      return this;
    }

    List<SendGridMailContent> allContent = mail.getContent();
    if (mail.getContent() == null) {
      allContent = new ArrayList<>();
      allContent.add(new SendGridMailContent(type, content));
      mail.setContent(allContent);
    } else {
      allContent.add(new SendGridMailContent(type, content));
    }
    return this;
  }

  private SendGridMailSettings getMailSetting() {
    SendGridMailSettings settings = mail.getMailSettings();
    if (settings == null) {
      settings = new SendGridMailSettings(
          new SendGridMailFooter(false, null, null),
          new SendGridSwitch(false)
      );
      mail.setMailSettings(settings);
    }
    return settings;
  }

  private SendGridPersonalizations getPersonalizations() {
    if (personalizations == null) {
      personalizations = new SendGridPersonalizations();
      mail.setPersonalizations(Collections.singletonList(personalizations));
    }
    return personalizations;
  }

  private SendGridTrackingSettings getTrackingSettings() {
    SendGridTrackingSettings trackingSettings = mail.getTrackingSettings();
    if (trackingSettings == null) {
      trackingSettings = new SendGridTrackingSettings(
          new SendGridSwitch(false),
          new SendGridSwitch(false),
          new SendGridSwitch(false)
      );
      mail.setTrackingSettings(trackingSettings);
    }
    return trackingSettings;
  }

  public SendGridMailBuilder from(@Nullable String email) {
    if (!Strings.isNullOrEmpty(email)) {
      mail.setFrom(new SendGridMailPerson(email));
    }
    return this;
  }

  public  SendGridMailBuilder subject(@Nullable String subject) {
    if (!Strings.isNullOrEmpty(subject)) {
      mail.setSubject(subject);
    }
    return this;
  }

  public SendGridMailBuilder addTo(@Nullable String email) {
    if (!Strings.isNullOrEmpty(email)) {
      getPersonalizations().addTo(new SendGridMailPerson(email));
    }
    return this;
  }
  public SendGridMailBuilder addBcc(@Nullable String email) {
    if (!Strings.isNullOrEmpty(email)) {
      getPersonalizations().addBcc(new SendGridMailPerson(email));
    }
    return this;
  }

  public SendGridMailBuilder addCc(@Nullable String email) {
    if (!Strings.isNullOrEmpty(email)) {
      getPersonalizations().addCc(new SendGridMailPerson(email));
    }
    return this;
  }

  public SendGridMailBuilder replyTo(@Nullable String email) {
    if (!Strings.isNullOrEmpty(email)) {
      mail.setReplyTo(new SendGridMailPerson(email));
    }
    return this;
  }

  public SendGridMailBuilder addTextContent(@Nullable String content) {
    return addContent("text/plain", content);
  }

  public SendGridMailBuilder addHtmlContent(@Nullable String content) {
    return addContent("text/html", content);
  }

  public SendGridMailBuilder sandboxMode(@Nullable Boolean enabled) {
    if (enabled != null) {
      getMailSetting().getSandboxMode().setEnable(enabled);
    }
    return this;
  }

  public SendGridMailBuilder footerText(@Nullable Boolean enabled, @Nullable String content) {
    if (enabled != null && !Strings.isNullOrEmpty(content)) {
      getMailSetting().setFooter(new SendGridMailFooter(enabled, content, null));
    }
    return this;
  }

  public SendGridMailBuilder footerHtml(@Nullable Boolean enabled, @Nullable String content) {
    if (enabled != null && !Strings.isNullOrEmpty(content)) {
      getMailSetting().setFooter(new SendGridMailFooter(enabled, null, content));
    }
    return this;
  }

  public SendGridMailBuilder clickTracking(@Nullable Boolean enabled) {
    if (enabled != null) {
      getTrackingSettings().getClickTracking().setEnable(enabled);
    }
    return this;
  }

  public SendGridMailBuilder openTracking(@Nullable Boolean enabled) {
    if (enabled != null) {
      getTrackingSettings().getOpenTracking().setEnable(enabled);
    }
    return this;
  }

  public SendGridMailBuilder subscriptionTracking(@Nullable Boolean enabled) {
    if (enabled != null) {
      getTrackingSettings().getSubscriptionTracking().setEnable(enabled);
    }
    return this;
  }

  public SendGridMail build() {
    return mail;
  }
}
