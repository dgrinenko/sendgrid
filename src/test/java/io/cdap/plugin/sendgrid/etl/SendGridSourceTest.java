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
package io.cdap.plugin.sendgrid.etl;

import com.google.common.collect.ImmutableMap;
import io.cdap.cdap.api.artifact.ArtifactSummary;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.dataset.table.Table;
import io.cdap.cdap.datapipeline.DataPipelineApp;
import io.cdap.cdap.datapipeline.SmartWorkflow;
import io.cdap.cdap.etl.api.batch.BatchSource;
import io.cdap.cdap.etl.mock.batch.MockSink;
import io.cdap.cdap.etl.mock.test.HydratorTestBase;
import io.cdap.cdap.etl.proto.v2.ETLBatchConfig;
import io.cdap.cdap.etl.proto.v2.ETLPlugin;
import io.cdap.cdap.etl.proto.v2.ETLStage;
import io.cdap.cdap.proto.ProgramRunStatus;
import io.cdap.cdap.proto.artifact.AppRequest;
import io.cdap.cdap.proto.id.ApplicationId;
import io.cdap.cdap.proto.id.ArtifactId;
import io.cdap.cdap.proto.id.NamespaceId;
import io.cdap.cdap.test.ApplicationManager;
import io.cdap.cdap.test.DataSetManager;
import io.cdap.cdap.test.WorkflowManager;
import io.cdap.plugin.sendgrid.BaseTest;
import io.cdap.plugin.sendgrid.batch.source.SendGridSource;
import io.cdap.plugin.sendgrid.batch.source.SendGridSourceConfig;
import io.cdap.plugin.sendgrid.common.SendGridClient;
import io.cdap.plugin.sendgrid.common.config.BaseConfig;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.objects.marketing.MarketingContacts;
import io.cdap.plugin.sendgrid.common.objects.marketing.MarketingNewContacts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SendGridSourceTest extends HydratorTestBase {
  private static final ArtifactSummary APP_ARTIFACT = new ArtifactSummary("data-pipeline", "3.2.0");
  private static final String LAST_NAME_FIELD = "last_name";
  private static final String ID_FIELD = "id";
  private static final String BASIC_AUTH_TYPE = "basic";
  private static final String REPLACE_PATTERN = "%id%";

  private static String authType;
  private static String authUser;
  private static String authPass;
  private static String authToken;

  private static SendGridClient client;
  private static MarketingNewContacts contacts;
  private static String reference;
  private static int createdContactsAmount;

  @BeforeClass
  public static void setupTestClass() throws Exception {
    reference = BaseTest.getRandomUUID();
    authType = System.getProperty("sg.auth.type");
    if (authType != null && authType.equals(BASIC_AUTH_TYPE)) {
      authUser = System.getProperty("sg.auth.user");
      authPass = System.getProperty("sg.auth.pass");

      if (authUser == null || authPass == null) {
        throw new IllegalArgumentException("'sg.auth.user' and 'sg.auth.pass' system property must not be empty");
      }
      client = new SendGridClient(authUser, authPass);
    } else {
      authToken = System.getProperty("sg.auth.token");
      if (authToken == null) {
        throw new IllegalArgumentException("'sg.auth.token' system property must not be empty");
      }
      client = new SendGridClient(authToken);
    }


    ArtifactId parentArtifact = NamespaceId.DEFAULT.artifact(APP_ARTIFACT.getName(), APP_ARTIFACT.getVersion());
    setupBatchArtifacts(parentArtifact, DataPipelineApp.class);
    addPluginArtifact(
        NamespaceId.DEFAULT.artifact("example-plugins", "1.0.0"),
        parentArtifact,
        SendGridSource.class
    );
    contacts = new MarketingNewContacts(BaseTest.getResource("new_contacts.csv"));
    createdContactsAmount = contacts.getContacts().size();

    contacts.getContacts().forEach(contact -> {
      contact.setLastName(reference);
      String mail = contact.getEmail();
      contact.setEmail(mail.replace(REPLACE_PATTERN, reference));
    });

    client.createContacts(contacts);
  }

  private static Stream<Map<String, Object>> getContacts() throws IOException {
    return client.getObject(ObjectHelper.getObjectInfo(MarketingContacts.class), null)
        .stream()
        .map(baseObject -> baseObject.asMap())
        .filter(fieldMap -> fieldMap.containsKey(LAST_NAME_FIELD) && fieldMap.get(LAST_NAME_FIELD).equals(reference));
  }

  @AfterClass
  public static void tearDownTestClass() throws IOException {
    List<String> idsToRemove = getContacts()
        .filter(fieldMap -> fieldMap.containsKey(ID_FIELD))
        .map(fieldMap -> (String) fieldMap.get(ID_FIELD))
        .filter(id -> id != null)
        .collect(Collectors.toList());
    client.deleteContacts(idsToRemove);
  }

  @Test
  public void testBatchSource() throws Exception {
    ImmutableMap.Builder<String, String> optionsBuilder = new ImmutableMap.Builder<>();

    if (authType == BASIC_AUTH_TYPE) {
      optionsBuilder
          .put(SendGridSourceConfig.PROPERTY_AUTH_TYPE, BASIC_AUTH_TYPE)
          .put(SendGridSourceConfig.PROPERTY_AUTH_USERNAME, authUser)
          .put(SendGridSourceConfig.PROPERTY_AUTH_PASSWORD, authPass);
    } else {
      optionsBuilder
          .put(SendGridSourceConfig.PROPERTY_AUTH_TYPE, "api")
          .put(SendGridSourceConfig.PROPERTY_SENDGRID_API_KEY, authToken);

    }

    optionsBuilder
        .put("referenceName", "ref")
        .put(SendGridSourceConfig.PROPERTY_DATA_SOURCE_TYPES, "MarketingCampaign")
        .put(SendGridSourceConfig.PROPERTY_DATA_SOURCE_MARKETING, "Contacts")
        .put(SendGridSourceConfig.PROPERTY_DATA_SOURCE_FIELDS, "created_at,email,first_name,last_name,updated_at");

    ETLStage source = new ETLStage("name", new ETLPlugin(BaseConfig.PLUGIN_NAME, BatchSource.PLUGIN_TYPE,
        optionsBuilder.build(), null));
    ETLStage sink = new ETLStage("sink", MockSink.getPlugin("outputSink"));

    ETLBatchConfig etlConfig = ETLBatchConfig.builder()
        .addStage(source)
        .addStage(sink)
        .addConnection(source.getName(), sink.getName())
        .build();

    ApplicationId pipelineId = NamespaceId.DEFAULT.app("HttpBatch_");
    ApplicationManager appManager = deployApplication(pipelineId, new AppRequest<>(APP_ARTIFACT, etlConfig));

    WorkflowManager workflowManager = appManager.getWorkflowManager(SmartWorkflow.NAME);
    workflowManager.startAndWaitForRun(ProgramRunStatus.COMPLETED, 5, TimeUnit.MINUTES);

    DataSetManager<Table> outputManager = getDataset("outputSink");
    List<StructuredRecord> outputRecords = MockSink.readOutput(outputManager);

    int contactCount = client.getObject(ObjectHelper.getObjectInfo(MarketingContacts.class), null).size();
    int retrievedContactsCount = (int) outputRecords.stream()
        .filter(x -> x.get("last_name").equals(reference))
        .count();

    Assert.assertEquals(contactCount, outputRecords.size());
    Assert.assertEquals(getContacts().count(), retrievedContactsCount);
  }
}
