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

package io.cdap.plugin.sendgrid.common;

/**
 * The way, how SendGrid API return the objects
 *
 */
public enum APIResponseType {
  /**
   * Objects come to the response as part of the list
   */
  LIST,

  /**
   * Objects come as part of wrapper, which consists from the metadata and the result
   */
  RESULT,

  /**
   * Same as {@link APIResponseType#RESULT}
   */
  RESULTS
}
