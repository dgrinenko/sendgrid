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

/**
 * GlobalUnsubscribes entity
 */
@ObjectDefinition(
  Name = "GlobalUnsubscribes",
  Group = DataSourceGroupType.Suppressions,
  APIUrl = "suppression/unsubscribes"
)
public class GlobalUnsubscribeSuppression extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.LONG)
  @SerializedName("created")
  public long created;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("email")
  public String email;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("created", created)
      .put("email", email)
      .build();
  }
}
