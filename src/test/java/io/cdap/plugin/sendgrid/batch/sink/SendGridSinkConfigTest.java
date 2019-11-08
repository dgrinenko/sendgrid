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
import io.cdap.plugin.sendgrid.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;


public class SendGridSinkConfigTest extends BaseTest {
  private static Gson gson = new GsonBuilder().create();
  SendGridSinkConfig config;

  @Before
  public void setUp() throws Exception {
    config = gson.fromJson(getResource("SendGridSinkConfigExample.json"), SendGridSinkConfig.class);
  }

  @Test
  public void testGetMailSubject() {
    Assert.assertEquals("subject", config.getMailSubject());
  }

  @Test
  public void testGetFrom() {
    Assert.assertEquals("test@email.com", config.getFrom());
  }

  @Test
  public void testGetRecipientAddressSource() {
    Assert.assertEquals(SendGridSinkConfig.ToAddressSource.INPUT, config.getRecipientAddressSource());
  }

  @Test
  public void testGetRecipientAddresses() {
    Assert.assertEquals(
      Arrays.asList("test1@email.com", "test2@email.com"),
      config.getRecipientAddresses()
    );
  }

  @Test
  public void testGetRecipientColumnName() {
    Assert.assertEquals("column1", config.getRecipientColumnName());
  }

  @Test
  public void testGetBodyColumnName() {
    Assert.assertEquals("column2", config.getBodyColumnName());
  }

  @Test
  public void testGetReplyTo() {
    Assert.assertEquals("reply@email.com", config.getReplyTo());
  }

  @Test
  public void testGetFooterEnable() {
    Assert.assertEquals(true, config.getFooterEnable());
  }

  @Test
  public void testGetFooterHTML() {
    Assert.assertEquals("footer", config.getFooterHTML());
  }

  @Test
  public void testGetSandboxMode() {
    Assert.assertEquals(true, config.getSandboxMode());
  }

  @Test
  public void testGetClickTracking() {
    Assert.assertEquals(true, config.getClickTracking());
  }

  @Test
  public void testGetOpenTracking() {
    Assert.assertEquals(true, config.getOpenTracking());
  }

  @Test
  public void testGetSubscriptionTracking() {
    Assert.assertEquals(true, config.getSubscriptionTracking());
  }
}
