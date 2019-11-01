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

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.api.data.batch.Output;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.cdap.api.dataset.lib.KeyValue;
import io.cdap.cdap.etl.api.Emitter;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.cdap.etl.api.PipelineConfigurer;
import io.cdap.cdap.etl.api.batch.BatchSink;
import io.cdap.cdap.etl.api.batch.BatchSinkContext;
import io.cdap.plugin.common.IdUtils;
import io.cdap.plugin.common.LineageRecorder;
import io.cdap.plugin.sendgrid.common.config.BaseConfig;
import io.cdap.plugin.sendgrid.common.objects.mail.SendGridMail;
import org.apache.hadoop.io.NullWritable;

import java.util.stream.Collectors;

/**
 * Batch Sink Plugin
 */
@Plugin(type = BatchSink.PLUGIN_TYPE)
@Name(BaseConfig.PLUGIN_NAME)
@Description("Sends mails via SendGrid")
public class SendGridSink extends BatchSink<StructuredRecord, NullWritable, SendGridMail> {

  private final SendGridSinkConfig config;

  public SendGridSink(SendGridSinkConfig config) {
    this.config = config;
  }

  @Override
  @SuppressWarnings("ThrowableNotThrown")
  public void configurePipeline(PipelineConfigurer pipelineConfigurer) {
    FailureCollector failureCollector = pipelineConfigurer.getStageConfigurer().getFailureCollector();

    IdUtils.validateReferenceName(config.referenceName, failureCollector);

    config.validate(failureCollector);
    config.validate(pipelineConfigurer.getStageConfigurer().getInputSchema());

    failureCollector.getOrThrowException();
  }

  @Override
  public void prepareRun(BatchSinkContext batchSinkContext) {
    Schema inputSchema = batchSinkContext.getInputSchema();
    config.validate(inputSchema);

    batchSinkContext.addOutput(Output.of(config.referenceName, new SendGridOutputFormatProvider(config)));

    LineageRecorder lineageRecorder = new LineageRecorder(batchSinkContext, config.referenceName);
    lineageRecorder.createExternalDataset(inputSchema);

    if (inputSchema.getFields() != null && !inputSchema.getFields().isEmpty()) {
      String operationDescription = String.format("Wrote to SendGrid %s", config.getFrom());
      lineageRecorder.recordWrite("Write", operationDescription,
          inputSchema.getFields().stream()
              .map(Schema.Field::getName)
              .collect(Collectors.toList()));
    }
  }

  @Override
  public void transform(StructuredRecord record, Emitter<KeyValue<NullWritable, SendGridMail>> emitter) {
    SendGridMail sendGridMail = SendGridSinkTransformer.transform(config, record);
    emitter.emit(new KeyValue<>(null, sendGridMail));
  }
}
