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
package io.cdap.plugin.sendgrid.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.sendgrid.BaseTest;
import io.cdap.plugin.sendgrid.common.objects.SendGridAuthType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Type;


public class BaseConfigTest extends BaseTest {
  /**
   * Custom class creator for abstract {@link BaseConfig}
   */
  private static class BaseConfigInstanceCreator implements InstanceCreator<BaseConfig> {

    private String ref;

    BaseConfigInstanceCreator(String ref) {
      this.ref = ref;
    }

    @Override
    public BaseConfig createInstance(Type type) {
      return new BaseConfig(ref) {
        @Override
        protected void validate(FailureCollector failureCollector) {
          // no-op
          throw new IllegalArgumentException("no op method");
        }
      };
    }
  }

  private BaseConfig basicAuthConfig;
  private BaseConfig keyAuthConfig;

  private static Gson gson;
  private static String refName = "ref" + getRandomUUID();

  @BeforeClass
  public static void classSetUp() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(BaseConfig.class, new BaseConfigInstanceCreator(refName));
    gson = builder.create();
  }

  @Before
  public void setUp() throws Exception {
    basicAuthConfig = gson.fromJson(getResource("BaseConfigExample.json"), BaseConfig.class);
    keyAuthConfig = gson.fromJson(getResource("KeyConfigExample.json"), BaseConfig.class);
   }

  @Test
  public void testReferenceValue() {
    Assert.assertEquals(refName, basicAuthConfig.referenceName);
    Assert.assertEquals(refName, keyAuthConfig.referenceName);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateBasic() {
    basicAuthConfig.validate(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateKey() {
    keyAuthConfig.validate(null);
  }

  @Test
  public void testGetAuthType() {
    Assert.assertEquals(SendGridAuthType.BASIC, basicAuthConfig.getAuthType());
    Assert.assertEquals(SendGridAuthType.API, keyAuthConfig.getAuthType());
  }

  @Test
  public void testGetSendGridApiKey() {
    Assert.assertEquals("some-api-key", keyAuthConfig.getSendGridApiKey());
  }

  @Test
  public void testGetAuthUserName() {
    Assert.assertEquals("test user", basicAuthConfig.getAuthUserName());
  }

  @Test
  public void testGetAuthPassword() {
    Assert.assertEquals("test pass", basicAuthConfig.getAuthPassword());
  }
}
