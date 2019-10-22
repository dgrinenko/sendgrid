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
package io.cdap.plugin.sendgrid.common.objects.marketing;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;
import io.cdap.plugin.sendgrid.common.helpers.ObjectFieldDefinition;
import io.cdap.plugin.sendgrid.common.objects.DataSourceGroupType;

import java.util.Map;

/**
 * Automation Entity
 */
@ObjectDefinition(
  Name = "Automation",
  Group = DataSourceGroupType.Marketing,
  APIUrl = "marketing/automations"
)
public class MarketingAutomation extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("id")
  private String id;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("name")
  private String name;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("type")
  private String type;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("status")
  private String status;

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @SerializedName("message_count")
  private Integer messageCount;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("created_at")
  private String createdAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("updated_at")
  private String updatedAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("live_at")
  private String liveAt;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", id)
      .put("name", name)
      .put("type", type)
      .put("status", status)
      .put("message_count", messageCount)
      .put("created_at", createdAt)
      .put("updated_at", updatedAt)
      .put("live_at", liveAt)
      .build();
  }
}
