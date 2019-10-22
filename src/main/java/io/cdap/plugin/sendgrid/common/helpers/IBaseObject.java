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

import io.cdap.cdap.api.data.schema.Schema;

import java.util.Map;

/**
 * Interface for all SendGrid objects
 *
 * Any child, implemented current interface should provide all
 * fields outside through {@link IBaseObject#asMap()} method.
 *
 * No getters allowed, unless any custom object usage planed.
 */
public interface IBaseObject {
  /**
   * Map of all object fields with values
   */
  Map<String, Object> asMap();

  /**
   * Provide access to object fields mentioned in {@link Schema}
   *
   * @param schema generated or customized schema for the object
   */
  Map<String, Object> asFilteredMap(Schema schema);
}
