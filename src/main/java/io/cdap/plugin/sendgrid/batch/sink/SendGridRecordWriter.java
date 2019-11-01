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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cdap.plugin.sendgrid.common.SendGridClient;
import io.cdap.plugin.sendgrid.common.objects.mail.SendGridMail;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import java.io.IOException;

/**
 * Writes {@link SendGridMail} into batches and submit them to SendGrid send API
 */
public class SendGridRecordWriter extends RecordWriter<NullWritable, SendGridMail> {
  private static final Gson gson = new GsonBuilder().create();
  private SendGridClient client;

  public SendGridRecordWriter(TaskAttemptContext taskAttemptContext) {
    Configuration conf = taskAttemptContext.getConfiguration();
    String serializedConfig = conf.get(SendGridOutputFormatProvider.PROPERTY_CONFIG_JSON);
    SendGridSinkConfig sgConfig  = gson.fromJson(serializedConfig, SendGridSinkConfig.class);

    client = new SendGridClient(sgConfig);
  }

  @Override
  public void write(NullWritable nullWritable, SendGridMail sendGridMail) throws IOException {
    client.sendMail(sendGridMail);
  }

  @Override
  public void close(TaskAttemptContext taskAttemptContext) {
    // no-op
  }
}
