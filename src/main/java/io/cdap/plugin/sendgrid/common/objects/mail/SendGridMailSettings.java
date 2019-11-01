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
 * Mail Settings
 */
public class SendGridMailSettings {
  @SerializedName("footer")
  private SendGridMailFooter footer;

  @SerializedName("sandbox_mode")
  private SendGridSwitch sandboxMode;

  public SendGridMailSettings(SendGridMailFooter footer, SendGridSwitch sandboxMode)  {
    this.footer = footer;
    this.sandboxMode = sandboxMode;
  }

  public SendGridMailFooter getFooter() {
    return footer;
  }

  public SendGridSwitch getSandboxMode() {
    return sandboxMode;
  }

  public void setFooter(SendGridMailFooter footer) {
    this.footer = footer;
  }

  public void setSandboxMode(SendGridSwitch sandboxMode) {
    this.sandboxMode = sandboxMode;
  }
}
