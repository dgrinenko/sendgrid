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

/**
 * SendgridPostAction implementation of {@link PostAction}.
 *
 * <p>
 *   {@link PostAction} are type of plugins that are executed in the
 *   Batch pipeline at the end of execution. Irrespective of the status of
 *   the pipeline this plugin will be invoked.
 *
 *   This type of plugin can be used to send notifications to external
 *   system or notify other workflows.
 * </p>
 */
@Plugin(type = PostAction.PLUGIN_TYPE)
@Name("Sendgrid")
@Description("An example of notification plugin.")
public final class SendgridPostAction extends PostAction {
  private final Config config;

  public static class Config extends ConditionConfig {
    @Description("Where email is being sent from")
    @Name("from")
    @Macro
    private String from;

    @Description("Subject of the email")
    @Name("subject")
    @Macro
    private String subject;

    @Description("Where email is being sent to")
    @Name("to")
    @Macro
    private String to;

    @Description("Content of the email")
    @Name("content")
    @Macro
    private String content;

    @Description("SendGrid API Key")
    @Name("apiKey")
    private String apiKey;
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
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      sg.api(request);
    } catch (Exception ex) {
      throw new RuntimeException("Error sending email: ", ex);
    }
  }
}
