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
import io.cdap.plugin.sendgrid.common.objects.BasicMetadata;
import io.cdap.plugin.sendgrid.common.objects.DataSourceGroupType;

import java.util.List;
import java.util.Map;


/**
 * Contacts entity
 */
@ObjectDefinition(
  Name = "Contacts",
  Group = DataSourceGroupType.Marketing,
  APIUrl = "marketing/contacts",
  APIResponseType = APIResponseType.RESULT
)
public class MarketingContacts extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("created_at")
  private String createdAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("email")
  private String email;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("first_name")
  private String firstName;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("id")
  private String id;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("last_name")
  private String lastName;

  @ObjectFieldDefinition(FieldType = Schema.Type.ARRAY)
  @SerializedName("list_ids")
  private List<String> ids;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("updated_at")
  private String updatedAt;

  @SerializedName("_metadata")
  private BasicMetadata metadata;


  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("created_at", createdAt)
      .put("email", email)
      .put("first_name", firstName)
      .put("id", id)
      .put("last_name", lastName)
      .put("list_ids", ids)
      .put("updated_at", updatedAt)
      .build();
  }

}
