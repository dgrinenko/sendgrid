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

/**
 * StatsStats entity
 */
@ObjectDefinition(
  Name = "StatsStats",
  ObjectType = ObjectDefinition.ObjectDefinitionType.NESTED
)
public class StatsStats extends BaseObject implements IBaseObject {

  @ObjectFieldDefinition(FieldType = Schema.Type.MAP, NestedClass = "MetricStats")
  @SerializedName("metrics")
  private MetricStats metrics;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("name")
  private String name;

  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  @SerializedName("type")
  private String type;

  @Override
  public Map<String, Object> asMap() {
    return new ImmutableMap.Builder<String, Object>()
      .put("metrics", metrics.asMap())
      .put("name", name)
      .put("type", type)
      .build();
  }
}
