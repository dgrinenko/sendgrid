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

package io.cdap.plugin.sendgrid.common.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Nullable;

/**
 * SendGrid API Response Wrapper
 *
 * @param <T> Any {@link io.cdap.plugin.sendgrid.common.helpers.BaseObject} object
 */
public class BasicResult<T> {
  @SerializedName(value = "result")
  private List<T> result;

  @SerializedName(value = "results")
  private List<T> results;

  @Nullable
  @SerializedName("_metadata")
  private BasicMetadata metadata;

  public List<T> getResult() {
    return (results == null) ? result : results;
  }

  @Nullable
  public BasicMetadata getMetadata() {
    return metadata;
  }
}
