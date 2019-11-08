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
package io.cdap.plugin.sendgrid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Base test class for SendGrid plugin
 */
public class BaseTest {

  /**
   * Read resource as {@link String}
   *
   * @param name resource name
   * @return resource string representation
   * @throws IOException in case if resource not found
   */
  public static String getResource(String name) throws IOException {
    ClassLoader classLoader = BaseTest.class.getClassLoader();

    try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
      if (inputStream == null) {
        throw new IOException(String.format("Error in reading file '%s'", name));
      }
      try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
        BufferedReader buffer = new BufferedReader(inputStreamReader);
        return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }

  /**
   * Returns random uuid without "-"
   */
  public static String getRandomUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
