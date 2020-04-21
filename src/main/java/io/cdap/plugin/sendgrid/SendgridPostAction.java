/*
 * Copyright © 2020 Cask Data, Inc.
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

package io.cdap.plugin.sendgrid;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.etl.api.PipelineConfigurer;
import io.cdap.cdap.etl.api.batch.BatchActionContext;
import io.cdap.cdap.etl.api.batch.PostAction;
import io.cdap.plugin.common.batch.action.ConditionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * SendgridPostAction implementation of {@link PostAction}.
 *
 * Sends an email to the specified email after a pipeline run using SendGrid service.
 */
@Plugin(type = PostAction.PLUGIN_TYPE)
@Name("EmailBySendgrid")
@Description("Sends an email at the end of a pipeline run. You can configure it to send an email when the pipeline " +
  "completes, succeeds or fails. Uses the Sendgrid service to send emails, and requires you to sign up for a " +
  "Sendgrid account.")
public final class SendgridPostAction extends PostAction {
  private static final Logger LOG = LoggerFactory.getLogger(SendgridPostAction.class);
  private final Config config;

  /**
   * Plugin config for Sendgrid post action.
   */
  public static class Config extends ConditionConfig {
    @Description("The address to send the email from.")
    @Name("from")
    @Macro
    private String from;

    @Description("The subject of the email")
    @Name("subject")
    @Macro
    private String subject;

    @Description("The address to send the email to.")
    @Name("to")
    @Macro
    private String to;

    @Description("The SendGrid API Key. After logging into your Sendgrid account, you can create a key in the " +
      "API keys section of the Settings page.")
    @Name("apiKey")
    @Macro
    private String apiKey;

    @Description("Optional content of the email. Defaults to empty.")
    @Name("content")
    @Macro
    @Nullable
    private String content;

    public Config() {
      if (content == null) {
        content = "";
      }
    }
  }

  public SendgridPostAction(Config config) {
    this.config = config;
  }

  @Override
  public void configurePipeline(PipelineConfigurer configurer) {
    super.configurePipeline(configurer);
  }

  @Override
  public void run(BatchActionContext context) throws Exception {
    // Framework provides the ability to decide within this plugin
    // whether this should be run or no. This happens depending on
    // the status of pipeline selected -- COMPLETION, SUCCESS or FAILURE.
    if (!config.shouldRun(context)) {
      return;
    }

    Email from = new Email(config.from);
    String subject = config.subject;
    Email to = new Email(config.to);
    Content content = new Content("text/plain", config.content);
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(config.apiKey);
    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    sg.api(request);
    LOG.debug("Sent email from {} to {} successfully", config.from, config.to);
  }
}
