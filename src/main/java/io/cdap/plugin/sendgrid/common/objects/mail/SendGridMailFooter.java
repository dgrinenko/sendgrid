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

import com.google.gson.annotations.SerializedName;

/**
 * Mail footer
 */
public class SendGridMailFooter {
  @SerializedName("enable")
  private Boolean enable;

  @SerializedName("text")
  private String text;

  @SerializedName("html")
  private String html;

  public SendGridMailFooter(Boolean enable, String text, String html) {
    this.enable = enable;
    this.text = text;
    this.html = html;
  }

  public Boolean getEnable() {
    return enable;
  }

  public String getText() {
    return text;
  }

  public String getHtml() {
    return html;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setHtml(String html) {
    this.html = html;
  }
}
