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

import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.sendgrid.common.objects.mail.SendGridMail;
import io.cdap.plugin.sendgrid.common.objects.mail.SendGridMailBuilder;

import java.util.Arrays;

/**
 * {@link StructuredRecord} to {@link SendGridMail}
 */
public class SendGridSinkTransformer {

  private static Object readRecordField(StructuredRecord record, String fieldName, Schema.Type fieldType) {
    Schema.Field field = record.getSchema().getField(fieldName);
    if (field == null) {
      throw new IllegalArgumentException(String.format("Input schema does not provide column '%s'", fieldName));
    }
    Schema fieldSchema = field.getSchema();

    if (fieldSchema.getType() == Schema.Type.UNION) {  // check the situation if field is nullable
      if (fieldSchema.getUnionSchemas().stream().noneMatch(x -> x.getType() == fieldType)) {
        throw new IllegalArgumentException(String.format("Column '%s' does not belong to type '%s'''",
            fieldName, fieldType.name()));
      }
    } else if (field.getSchema().getType() != fieldType) {
      throw new IllegalArgumentException(String.format("Column '%s' does not belong to type '%s'''",
          fieldName, fieldType.name()));
    }
    Object objRecipients = record.get(fieldName);
    if (objRecipients == null) {
      throw new IllegalArgumentException("Record provided empty list of recipients");
    }
    return objRecipients;
  }

  public static SendGridMail transform(SendGridSinkConfig config, StructuredRecord record) {
    SendGridMailBuilder builder = SendGridMailBuilder.getInstance();

    builder.from(config.getFrom());
    if (config.getRecipientAddressSource() == SendGridSinkConfig.ToAddressSource.CONFIG) {
      config.getRecipientAddresses().forEach(builder::addTo);
    } else if (config.getRecipientAddressSource() == SendGridSinkConfig.ToAddressSource.INPUT) {
      Object recipientFieldValue = readRecordField(record, config.getRecipientColumnName(), Schema.Type.STRING);
      Arrays
          .asList(((String) recipientFieldValue).split(","))
          .forEach(builder::addTo);
    }
    builder.replyTo(config.getReplyTo());
    builder.subject(config.getMailSubject());

    Object bodyValue = readRecordField(record, config.getBodyColumnName(), Schema.Type.STRING);
    builder.addHtmlContent((String) bodyValue);
    builder.footerHtml(config.getFooterEnable(), config.getFooterHTML());

    builder.clickTracking(config.getClickTracking());
    builder.openTracking(config.getOpenTracking());
    builder.subscriptionTracking(config.getSubscriptionTracking());
    builder.sandboxMode(config.getSandboxMode());

    return builder.build();
  }
}
