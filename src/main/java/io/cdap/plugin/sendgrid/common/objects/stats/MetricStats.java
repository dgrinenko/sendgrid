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
package io.cdap.plugin.sendgrid.common.objects.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;
import io.cdap.plugin.sendgrid.common.helpers.ObjectFieldDefinition;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * MetricStats entity
 */
@ObjectDefinition(
  Name = "MetricStats",
  ObjectType = ObjectDefinition.ObjectDefinitionType.NESTED
)
public class MetricStats extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("blocks")
  private Integer blocks;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("bounce_drops")
  private Integer bounceDrops;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("bounces")
  private Integer bounces;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("clicks")
  private Integer clicks;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("deferred")
  private Integer deferred;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("invalid_emails")
  private Integer invalidEmails;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("opens")
  private Integer opens;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("processed")
  private Integer processed;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("requests")
  private Integer requests;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("spam_report_drops")
  private Integer spamReportDrops;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("spam_reports")
  private Integer spamReports;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("unique_clicks")
  private Integer uniqueClicks;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("unique_opens")
  private Integer uniqueOpens;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("unsubscribe_drops")
  private Integer unsubscribeDrops;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @Nullable
  @SerializedName("unsubscribes")
  private Integer unsubscribes;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("blocks", (blocks == null) ? 0 : blocks)
      .put("bounce_drops", (bounceDrops == null) ? 0 : bounceDrops)
      .put("bounces", (bounces == null) ? 0 : bounces)
      .put("clicks", (clicks == null) ? 0 : clicks)
      .put("deferred", (deferred == null) ? 0 : deferred)
      .put("invalid_emails", (invalidEmails == null) ? 0 : invalidEmails)
      .put("opens", (opens == null) ? 0 : opens)
      .put("processed", (processed == null) ? 0 : processed)
      .put("requests", (requests == null) ? 0 : requests)
      .put("spam_report_drops", (spamReportDrops == null) ? 0 : spamReportDrops)
      .put("spam_reports", (spamReports == null) ? 0 : spamReports)
      .put("unique_clicks", (uniqueClicks == null) ? 0 : uniqueClicks)
      .put("unique_opens", (uniqueOpens == null) ? 0 : uniqueOpens)
      .put("unsubscribe_drops", (unsubscribeDrops == null) ? 0 : unsubscribeDrops)
      .put("unsubscribes", (unsubscribes == null) ? 0 : unsubscribes)
      .build();
  }
}
