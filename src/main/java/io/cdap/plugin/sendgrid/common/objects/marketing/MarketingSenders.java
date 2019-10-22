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
 * Senders entity
 */
@ObjectDefinition(
  Name = "Senders",
  Group = DataSourceGroupType.Marketing,
  APIUrl = "mc/senders"
)
public class MarketingSenders extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("address")
  private String address;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("address_2")
  private String address2;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("city")
  private String city;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("country")
  private String country;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("created_at")
  private String createdAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.MAP, NestedClass = "MarketingSendersContact")
  @SerializedName("from")
  private MarketingSendersContact from;

  @ObjectFieldDefinition(FieldType = Schema.Type.LONG)
  @SerializedName("id")
  private long id;

  @ObjectFieldDefinition(FieldType = Schema.Type.BOOLEAN)
  @SerializedName("locked")
  private boolean locked;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("nickname")
  private String nickname;

  @ObjectFieldDefinition(FieldType = Schema.Type.MAP, NestedClass = "MarketingSendersContact")
  @SerializedName("reply_to")
  private MarketingSendersContact replyTo;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("state")
  private String state;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("updated_at")
  private String updatedAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.MAP, NestedClass = "MarketingSendersVerified")
  @SerializedName("verified")
  private MarketingSendersVerified verified;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("zip")
  private String zip;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("address", address)
      .put("address_2", address2)
      .put("city", city)
      .put("country", country)
      .put("created_at", createdAt)
      .put("from", from.asMap())
      .put("id", id)
      .put("locked", locked)
      .put("nickname", nickname)
      .put("reply_to", replyTo.asMap())
      .put("state", state)
      .put("updated_at", updatedAt)
      .put("verified", verified)
      .put("zip", zip)
      .build();
  }
}
