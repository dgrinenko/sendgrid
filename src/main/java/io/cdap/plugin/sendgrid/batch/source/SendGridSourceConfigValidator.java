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
package io.cdap.plugin.sendgrid.batch.source;

import com.google.common.base.Strings;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.sendgrid.batch.sink.SendGridSinkConfig;
import io.cdap.plugin.sendgrid.common.config.BaseConfigValidator;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.helpers.ObjectInfo;
import io.cdap.plugin.sendgrid.common.objects.DataSourceGroupType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validates configuration
 */
public class SendGridSourceConfigValidator extends BaseConfigValidator {
  private static Pattern datePattern = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{1,2})");
  private SendGridSourceConfig config;

  public SendGridSourceConfigValidator(FailureCollector failureCollector, SendGridSourceConfig config) {
    super(failureCollector, config);
    this.config = config;
  }

  private Stream<ObjectInfo> getObjectsStream() {
    return config.getDataSource().stream()
        .filter(x -> !Strings.isNullOrEmpty(x))
        .map(ObjectHelper::getObjectInfo);
  }

  private void checkCategoriesSelection() {
    if (config.getDataSourceTypes().isEmpty()) {
      failureCollector.addFailure("Object categories are not set", null)
          .withConfigProperty(SendGridSourceConfig.PROPERTY_DATA_SOURCE_TYPES);
    }

    config.getDataSourceTypes()
        .forEach(x -> {
          try {
            DataSourceGroupType.fromString(x);
          } catch (IllegalStateException e) {
            failureCollector.addFailure(
                String.format("Unknown '%s' data source type: %s", x , e.getMessage()), null)
                .withStacktrace(e.getStackTrace());
          }
        });
  }

  private void checkObjectsSelection() {
    List<ObjectInfo> objects =  getObjectsStream().collect(Collectors.toList());
    List<DataSourceGroupType> categories = config.getDataSourceTypes().stream()
        .map(DataSourceGroupType::fromString)
        .collect(Collectors.toList());

    categories.forEach(category -> {
      if (objects.stream().noneMatch(x -> x.getDataSourceGroupType() == category)) {
        failureCollector.addFailure(
            String.format("No objects selected for the category: %s", category.name()), null)
            .withConfigProperty(SendGridSourceConfig.PROPERTY_DATA_SOURCE);
      }
    });
  }

  private void checkFieldSelection() {
    List<ObjectInfo> objects = getObjectsStream().collect(Collectors.toList());
    List<String> fields = config.getFields();

    objects.forEach(object -> {
      if (object.getFieldsDefinitions(fields).isEmpty()) {
        failureCollector.addFailure(
            String.format("No fields selected for object '%s'", object.getCdapObjectName()), null)
            .withConfigProperty(SendGridSourceConfig.PROPERTY_DATA_SOURCE_FIELDS);
      }
    });
  }

  private void checkRequiredInputProperties() {
    getObjectsStream()
        .flatMap(obj -> obj.getRequiredArguments().stream())
        .filter(arg -> !config.getRequestArguments().containsKey(arg))
        .forEach(arg -> failureCollector.addFailure(
            String.format("Argument %s cannot be empty", arg), null)
            .withConfigProperty(arg));
  }

  private void checkDateFormat(String date, String field) {
    if (!Strings.isNullOrEmpty(date)) {
      Matcher matcher = datePattern.matcher(date);
      if (!matcher.find()) {
        failureCollector.addFailure("Input format should math YYYY-MM-DD", null)
            .withConfigProperty(field);
      } else {
        Integer month;
        Integer day;

        try {
          month = Integer.valueOf(matcher.group("month"));
          day = Integer.valueOf(matcher.group("day"));
        } catch (NumberFormatException e) {
          failureCollector.addFailure("Input format should math YYYY-MM-DD", null)
              .withConfigProperty(field);
          return;
        }

        if (month < 1 || month > 12) {
          failureCollector.addFailure(
              "Input format should math YYYY-MM-DD and MM should be in range from 1 to 12", null)
              .withConfigProperty(field);
        }

        if (day < 1 || day > 31) {
          failureCollector.addFailure(
              "Input format should math YYYY-MM-DD and DD should be in range from 1 to 31", null)
              .withConfigProperty(field);
        }
      }
    }
  }

  private void checkDateArguments() {
    checkDateFormat(config.getStartDate(), SendGridSourceConfig.PROPERTY_START_DATE);
    checkDateFormat(config.getEndDate(), SendGridSourceConfig.PROPERTY_END_DATE);
  }

  @Override
  public void doValidation() {
    if (!config.containsMacro(SendGridSourceConfig.PROPERTY_DATA_SOURCE_TYPES)) {
      checkCategoriesSelection();
    }
    if (!config.containsMacro(SendGridSourceConfig.PROPERTY_DATA_SOURCE)) {
      checkObjectsSelection();
    }
    if (!config.containsMacro(SendGridSourceConfig.PROPERTY_DATA_SOURCE_FIELDS)) {
      checkFieldSelection();
    }
    if (!config.containsMacro(SendGridSourceConfig.PROPERTY_STAT_CATEGORIES) &&
        !config.containsMacro(SendGridSourceConfig.PROPERTY_START_DATE) &&
        !config.containsMacro(SendGridSourceConfig.PROPERTY_END_DATE)) {

      checkRequiredInputProperties();
    }

    if (!config.containsMacro(SendGridSourceConfig.PROPERTY_START_DATE) &&
        !config.containsMacro(SendGridSourceConfig.PROPERTY_END_DATE)) {
      checkDateArguments();
    }
  }
}
