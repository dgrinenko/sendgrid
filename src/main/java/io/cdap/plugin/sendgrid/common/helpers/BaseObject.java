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
package io.cdap.plugin.sendgrid.common.helpers;

import com.google.common.collect.ImmutableMap;
import io.cdap.cdap.api.data.schema.Schema;

import java.util.Map;
import java.util.Objects;


/**
 * Base object for the {@link IBaseObject} interface
 */
public abstract class BaseObject implements IBaseObject {

  /**
   * Return Map of fields according to provided schema
   *
   * @param schema object schema
   * @return fields map
   */
  @Override
  public Map<String, Object> asFilteredMap(Schema schema) {
    ImmutableMap.Builder<String, Object> fields = new ImmutableMap.Builder<>();
    Map<String, Object> allFields = asMap();

    Objects.requireNonNull(schema.getFields()).stream()
        .map(Schema.Field::getName)
        .forEach(name -> fields.put(name, allFields.getOrDefault(name, new EmptyObject())));

    return fields.build();
  }
}
