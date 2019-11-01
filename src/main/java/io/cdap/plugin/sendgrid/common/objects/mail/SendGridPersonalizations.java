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

import java.util.ArrayList;
import java.util.List;

/**
 * Personalization Object
 */
public class SendGridPersonalizations {
  @SerializedName("to")
  private List<SendGridMailPerson> to;

  @SerializedName("cc")
  private List<SendGridMailPerson> cc;

  @SerializedName("bcc")
  private List<SendGridMailPerson> bcc;

  public void addTo(SendGridMailPerson value) {
    if (to == null) {
      to = new ArrayList<>();
    }
    to.add(value);
  }

  public void addCc(SendGridMailPerson value) {
    if (cc == null) {
      cc = new ArrayList<>();
    }
    cc.add(value);
  }

  public void addBcc(SendGridMailPerson value) {
    if (bcc == null) {
      bcc = new ArrayList<>();
    }
    bcc.add(value);
  }

  public List<SendGridMailPerson> getTo() {
    return to;
  }

  public List<SendGridMailPerson> getCc() {
    return cc;
  }

  public List<SendGridMailPerson> getBcc() {
    return bcc;
  }
}
