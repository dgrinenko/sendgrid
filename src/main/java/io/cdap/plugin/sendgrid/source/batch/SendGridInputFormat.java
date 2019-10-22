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
package io.cdap.plugin.sendgrid.source.batch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * SendGrid InputFormat
 */
public class SendGridInputFormat extends InputFormat {
  private static final Gson gson = new GsonBuilder().create();

  @Override
  public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
    return Collections.singletonList(new SendGridSplit());
  }

  @Override
  public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context)
    throws IOException, InterruptedException {

    Configuration conf = context.getConfiguration();
    String serializedConfig = conf.get(SendGridInputFormatProvider.PROPERTY_CONFIG_JSON);
    SendGridBatchSourceConfig sgConfig  = gson.fromJson(serializedConfig, SendGridBatchSourceConfig.class);

    return (sgConfig.isMultiObjectMode())
      ? new SendGridMultiRecordReader()
      : new SendGridRecordReader();
  }
}
