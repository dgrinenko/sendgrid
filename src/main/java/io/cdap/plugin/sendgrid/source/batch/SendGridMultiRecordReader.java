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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cdap.plugin.sendgrid.common.SendGridClient;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.MultiObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.helpers.ObjectInfo;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SendGrid MultiRecord Reader
 */
public class SendGridMultiRecordReader extends RecordReader<NullWritable, IBaseObject> {
  private static final Gson gson = new GsonBuilder().create();

  private Map<String, Iterator<IBaseObject>> recordIterators;
  private Map<String, IBaseObject> currentRecords;

  @Override
  public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
    String serializedConfig = context.getConfiguration().get(SendGridInputFormatProvider.PROPERTY_CONFIG_JSON);
    SendGridBatchConfig sgConfig  = gson.fromJson(serializedConfig, SendGridBatchConfig.class);
    SendGridClient client = new SendGridClient(sgConfig);
    ImmutableMap.Builder<String, Iterator<IBaseObject>> iterators = new ImmutableMap.Builder<>();

    sgConfig.getDataSource().forEach(x -> {
      ObjectInfo objectInfo = ObjectHelper.getObjectInfo(x);
      try {
        iterators.put(x, client.getObject(objectInfo, sgConfig.getRequestArguments()).iterator());
      } catch (IOException e) {
        iterators.put(x, Collections.emptyIterator());
      }
    });
    recordIterators = iterators.build();
    currentRecords = new HashMap<>();
  }

  @Override
  public boolean nextKeyValue() throws IOException, InterruptedException {
    currentRecords.clear();

    return recordIterators.entrySet().stream()
      .map(k -> {
        boolean currHasNext = k.getValue().hasNext();
        if (currHasNext) {
          currentRecords.put(k.getKey(), k.getValue().next());
        }
        return currHasNext;
       })
      .reduce(false, (prev, curr) -> prev || curr);
  }

  @Override
  public NullWritable getCurrentKey() throws IOException, InterruptedException {
    return null;
  }

  @Override
  public IBaseObject getCurrentValue() throws IOException, InterruptedException {
    MultiObject multiObject = new MultiObject();
    currentRecords.forEach(multiObject::addObject);

    return multiObject;
  }

  @Override
  public float getProgress() throws IOException, InterruptedException {
    return 0.0f;
  }

  @Override
  public void close() throws IOException {
   // no-op
  }
}
