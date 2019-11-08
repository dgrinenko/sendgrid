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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cdap.plugin.sendgrid.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class SendGridSourceConfigTest extends BaseTest {
  private static Gson gson = new GsonBuilder().create();
  SendGridSourceConfig config;

  @Before
  public void setUp() throws Exception {
    config = gson.fromJson(getResource("SendGridSourceConfigExample.json"), SendGridSourceConfig.class);
  }

  @Test
  public void getFields() {
    Assert.assertEquals(
      Arrays.asList("address", "city", "contracts_count"),
      config.getFields()
    );
  }

  @Test
  public void getDataSource() {
    Assert.assertEquals(
      Arrays.asList("SingleSends", "Senders", "CategoryStats", "Bounces", "GlobalUnsubscribes"),
      config.getDataSource()
    );
  }

  @Test
  public void isMultiObjectMode() {
    Assert.assertTrue(config.isMultiObjectMode());
  }

  @Test
  public void getRequestArguments() {
    Assert.assertEquals(
      new ImmutableMap.Builder<String, String>()
        .put(SendGridSourceConfig.PROPERTY_START_DATE, "2019-09-18")
        .put(SendGridSourceConfig.PROPERTY_END_DATE, "2019-09-21")
        .put(SendGridSourceConfig.PROPERTY_STAT_CATEGORIES, "spam")
       .build(),
      config.getRequestArguments()
    );
  }

  @Test
  public void getDataSourceTypes() {
    Assert.assertEquals(
      Arrays.asList("MarketingCampaign", "Statistic", "Suppression"),
      config.getDataSourceTypes()
    );
  }
}
