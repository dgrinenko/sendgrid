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
import io.cdap.plugin.sendgrid.common.APIResponseType;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;
import io.cdap.plugin.sendgrid.common.helpers.ObjectFieldDefinition;
import io.cdap.plugin.sendgrid.common.objects.DataSourceGroupType;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Segments entity
 */
@ObjectDefinition(
  Name = "Segments",
  Group = DataSourceGroupType.Marketing,
  APIUrl = "marketing/segments",
  APIResponseType = APIResponseType.RESULT
)
public class MarketingSegments extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  @SerializedName("contracts_count")
  private int contractsCount;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("created_at")
  private String createdAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("id")
  private String id;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("name")
  private String name;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("parent_list_id")
  @Nullable
  private String parentListId;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("sample_updated_at")
  private String sampleUpdatedAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("updated_at")
  private String updatedAt;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("contacts_count", contractsCount)
      .put("created_at", createdAt)
      .put("id", id)
      .put("name", name)
      .put("parent_list_id", (parentListId == null) ? "" : parentListId)
      .put("sample_updated_at", sampleUpdatedAt)
      .put("updated_at", updatedAt)
      .build();
  }
}
