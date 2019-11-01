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
package io.cdap.plugin.sendgrid.batch.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cdap.plugin.sendgrid.common.SendGridClient;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.helpers.ObjectInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * SendGrid Record Reader
 */
public class SendGridRecordReader extends RecordReader<NullWritable, IBaseObject> {
  private static final Gson gson = new GsonBuilder().create();

  private Iterator<IBaseObject> recordIterator;
  private IBaseObject currentRecord;

  @Override
  public void initialize(InputSplit split, TaskAttemptContext context) throws IOException {
    Configuration conf = context.getConfiguration();
    String serializedConfig = conf.get(SendGridInputFormatProvider.PROPERTY_CONFIG_JSON);
    SendGridSourceConfig sgConfig  = gson.fromJson(serializedConfig, SendGridSourceConfig.class);

    SendGridClient client = new SendGridClient(sgConfig);

    Iterator<String> objectsIterator = sgConfig.getDataSource().iterator();
    if (objectsIterator.hasNext()) {
      ObjectInfo currentObject = ObjectHelper.getObjectInfo(objectsIterator.next());
      recordIterator = client.getObject(currentObject, sgConfig.getRequestArguments()).iterator();
    } else {
      recordIterator = Collections.emptyIterator();
    }
  }

  @Override
  public boolean nextKeyValue() {
    boolean recordHasNext = recordIterator.hasNext();

    if (recordHasNext) {
      currentRecord = recordIterator.next();
    }
    return recordHasNext;
  }

  @Override
  public NullWritable getCurrentKey() {
    return null;
  }

  @Override
  public IBaseObject getCurrentValue() {
    return currentRecord;
  }

  @Override
  public float getProgress() {
    return 0.0f;
  }

  @Override
  public void close() {
   // no-op
  }
}
