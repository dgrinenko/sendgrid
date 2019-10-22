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
package io.cdap.plugin.sendgrid.common.objects.suppressions;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;
import io.cdap.plugin.sendgrid.common.helpers.ObjectFieldDefinition;
import io.cdap.plugin.sendgrid.common.objects.DataSourceGroupType;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * GroupUnsubscribes entity
 */
@ObjectDefinition(
  Name = "GroupUnsubscribes",
  Group = DataSourceGroupType.Suppressions,
  APIUrl = "asm/groups"
)
public class GroupUnsubscribeSuppression extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("description")
  private String description;

  @ObjectFieldDefinition(FieldType = Schema.Type.LONG)
  @SerializedName("id")
  private long id;

  @ObjectFieldDefinition(FieldType = Schema.Type.BOOLEAN)
  @SerializedName("is_default")
  private boolean isDefault;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @Nullable
  @SerializedName("last_email_sent_at")
  private String lastEmailSentAt;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("name")
  private String name;

  @ObjectFieldDefinition(FieldType = Schema.Type.LONG)
  @SerializedName("unsubscribes")
  private long unsubscribes;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("description", description)
      .put("id", id)
      .put("is_default", isDefault)
      .put("last_email_sent_at", (lastEmailSentAt == null) ? "" : lastEmailSentAt)
      .put("name", name)
      .put("unsubscribes", unsubscribes)
      .build();
  }
}
