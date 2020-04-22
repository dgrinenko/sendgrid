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
import io.cdap.cdap.api.workflow.WorkflowToken;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.cdap.etl.api.PipelineConfigurer;
import io.cdap.cdap.etl.api.batch.BatchActionContext;
import io.cdap.cdap.etl.api.batch.PostAction;
import io.cdap.plugin.common.batch.action.ConditionConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String SUBJECT = "subject";
    private static final String CONTENT = "content";
    private static final String API_KEY = "apiKey";
    private static final String INCLUDE_WORKFLOW_TOKEN = "includeWorkflowToken";

    @Description("The address to send the email from. The identity of this email must be verified with Sendgrid " +
      "using one of the options available at https://sendgrid.com/docs/for-developers/sending-email/sender-identity/.")
    @Name(FROM)
    @Macro
    private String from;

    @Description("The subject of the email")
    @Name(SUBJECT)
    @Macro
    private String subject;

    @Description("The address to send the email to.")
    @Name(TO)
    @Macro
    private String to;

    @Description("The SendGrid API Key. After logging into your Sendgrid account, you can create a key in the " +
      "API keys section of the Settings page.")
    @Name(API_KEY)
    @Macro
    private String apiKey;

    @Description("Optional content of the email. Defaults to empty.")
    @Name(CONTENT)
    @Macro
    @Nullable
    private String content;

    @Name(INCLUDE_WORKFLOW_TOKEN)
    @Description("Workflow tokens contain data such as counters, statuses and metrics, that are passed between the " +
      "nodes of a pipeline. This config determines whether to include the contents of the workflow token in the " +
      "email message. Defaults to false.")
    @Macro
    @Nullable
    private Boolean includeWorkflowToken;

    public Config() {
      if (content == null) {
        content = "";
      }
      if (includeWorkflowToken == null) {
        includeWorkflowToken = false;
      }
    }

    public void validate(FailureCollector collector) {
      if (!containsMacro(FROM)) {
        try {
          InternetAddress[] addresses = InternetAddress.parse(from);
          if (addresses.length != 1) {
            collector.addFailure(
              String.format("Exactly one sender is supported, but found %s.", addresses.length),
              "Only specify one sender email address.").withConfigProperty(FROM);
          }
        } catch (AddressException e) {
          collector.addFailure(
            String.format("%s is an invalid sender email address. Reason: %s", from, e.getMessage()),
            "Enter a valid email address for the sender."
          ).withConfigProperty(FROM);
        }
      }

      if (!containsMacro(TO)) {
        try {
          InternetAddress[] addresses = InternetAddress.parse(to);
          if (addresses.length != 1) {
            collector.addFailure(
              String.format("Exactly one recipient is supported, but found %s.", addresses.length),
              "Only specify one recipient email address."
            ).withConfigProperty(TO);
          }
        } catch (AddressException e) {
          collector.addFailure(
            String.format("%s is an invalid recipient email address. Reason: %s", to, e.getMessage()),
            "Enter a valid email address for the recipient."
          ).withConfigProperty(TO);
        }
      }
    }
  }

  public SendgridPostAction(Config config) {
    this.config = config;
  }

  @Override
  public void configurePipeline(PipelineConfigurer configurer) {
    super.configurePipeline(configurer);
    FailureCollector failureCollector = configurer.getStageConfigurer().getFailureCollector();
    config.validate(failureCollector);
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
    StringBuilder emailText = new StringBuilder(config.content)
      .append("\n\n");
    if (config.includeWorkflowToken) {
      WorkflowToken token = context.getToken();
      emailText.append("\nUSER Workflow Tokens:\n")
        .append(token.getAll(WorkflowToken.Scope.USER))
        .append("\nSYSTEM Workflow Tokens:\n")
        .append(token.getAll(WorkflowToken.Scope.SYSTEM))
        .append("\n");
    }
    Content content = new Content("text/plain", emailText.toString());
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
