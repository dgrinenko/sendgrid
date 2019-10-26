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
package io.cdap.plugin.sendgrid.common.config;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.plugin.common.ReferencePluginConfig;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;
import io.cdap.plugin.sendgrid.common.helpers.ObjectHelper;
import io.cdap.plugin.sendgrid.common.objects.SendGridAuthType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Provides all required configuration for reading SendGrid information
 */
public class BaseConfig extends ReferencePluginConfig {
  public static final String PROPERTY_AUTH_TYPE = "authType";
  public static final String PROPERTY_SENDGRID_API_KEY = "sendGridApiKey";
  public static final String PROPERTY_AUTH_USERNAME = "username";
  public static final String PROPERTY_AUTH_PASSWORD = "password";
  public static final String PROPERTY_DATA_SOURCE_TYPES = "dataSourceTypes";
  public static final String PROPERTY_DATA_SOURCE = "dataSource";
  public static final String PROPERTY_DATA_SOURCE_MARKETING = PROPERTY_DATA_SOURCE + "Marketing";
  public static final String PROPERTY_DATA_SOURCE_STATS = PROPERTY_DATA_SOURCE + "Stats";
  public static final String PROPERTY_DATA_SOURCE_SUPPRESSIONS = PROPERTY_DATA_SOURCE + "Suppressions";

  public static final String PROPERTY_DATA_SOURCE_FIELDS = "dataSourceFields";
  public static final String PROPERTY_STAT_CATEGORIES = "statCategories";
  public static final String PROPERTY_START_DATE = "start_date";
  public static final String PROPERTY_END_DATE = "end_date";

  @Name(PROPERTY_AUTH_TYPE)
  @Description("The way, how user would like to be authenticated to the SendGrid account")
  @Macro
  private String authType;

  @Name((PROPERTY_SENDGRID_API_KEY))
  @Description("The SendGrid API Key taken from the SendGrid account")
  @Macro
  @Nullable
  private String sendGridApiKey;

  @Name(PROPERTY_AUTH_USERNAME)
  @Description("Login name for the SendGrid account")
  @Macro
  @Nullable
  private String authUserName;

  @Name(PROPERTY_AUTH_PASSWORD)
  @Description("Password for the SendGrid account")
  @Macro
  @Nullable
  private String authPassword;

  @Name(PROPERTY_DATA_SOURCE_TYPES)
  @Description("List of data source groups")
  @Macro
  private String dataSourceTypes;

  @Name(PROPERTY_DATA_SOURCE_MARKETING)
  @Description("SendGrid source objects for the Marketing group")
  @Macro
  @Nullable
  private String dataSourceMarketing;

  @Name(PROPERTY_DATA_SOURCE_STATS)
  @Description("SendGrid source objects for the Statistics group")
  @Macro
  @Nullable
  private String dataSourceStats;

  @Name(PROPERTY_DATA_SOURCE_SUPPRESSIONS)
  @Description("SendGrid source objects for the Suppressions group")
  @Macro
  @Nullable
  private String dataSourceSuppressions;

  @Name(PROPERTY_DATA_SOURCE_FIELDS)
  @Description("The list of fields available for the retrieval")
  @Macro
  @Nullable
  private String dataSourceFields;

  @Name(PROPERTY_START_DATE)
  @Description("The date in format YYYY-MM-DD, starting from which the data is requested")
  @Nullable
  @Macro
  private String startDate;

  @Name(PROPERTY_END_DATE)
  @Description("The date in format YYYY-MM-DD, the end date for the requested data")
  @Nullable
  @Macro
  private String endDate;

  @Name(PROPERTY_STAT_CATEGORIES)
  @Description("List of requested categories for the CategoryStats request")
  @Nullable
  @Macro
  private String statCategories;

  private transient Schema schema;
  private transient List<String> dataSource;
  private transient Boolean multiObjectMode;

  /**
   * Constructor
   *
   * @param referenceName uniquely identify source/sink for lineage, annotating metadata, etc.
   */
  public BaseConfig(String referenceName) {
    super(referenceName);
  }

  /**
   * Validate configuration for the issues
   */
  protected void validate(FailureCollector failureCollector) {
     new BaseConfigValidator(failureCollector, this).validate();
  }

  /**
   * Fetches all fields selected by the user
   */
  public List<String> getFields() {
    if (Strings.isNullOrEmpty(dataSourceFields)) {
      return Collections.emptyList();
    }

    return Arrays.asList(dataSourceFields.split(","));
  }

  /**
   * Aggregates categorized data source
   */
  public List<String> getDataSource() {
    if (dataSource == null) {
      ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

      if (!Strings.isNullOrEmpty(dataSourceMarketing)) {
        builder.add(dataSourceMarketing);
      }
      if (!Strings.isNullOrEmpty(dataSourceStats)) {
        builder.add(dataSourceStats);
      }
      if (!Strings.isNullOrEmpty(dataSourceSuppressions)) {
        builder.add(dataSourceSuppressions);
      }

      dataSource = Arrays.asList(String.join(",", builder.build()).split(","));
    }
    return dataSource;
  }

  /**
   * Plugin work mode
   */
  public boolean isMultiObjectMode() {
    if (multiObjectMode == null) {
      multiObjectMode = getDataSource().size() > 1;
    }
    return multiObjectMode;
  }

  /**
   * Generated schema according to user configuration
   *
   * @return user configured schema
   */
  public Schema getSchema() {
    if (schema == null) {
      schema = ObjectHelper.buildSchema(getDataSource(), getFields());
    }
    return schema;
  }

  /**
   * Generates limited schema for mentioned in {@code dataSource} sources
   *
   * @param dataSource sources to be added to the schema
   *
   * @return custom schema
   */
  public Schema getSchema(List<String> dataSource) {
    return ObjectHelper.buildSchema(dataSource, getFields(), isMultiObjectMode());
  }

  /**
   * Returns query properties required for some SendGrid Objects
   * marked with {@link ObjectDefinition#RequiredArguments()}
   */
  public Map<String, String> getRequestArguments() {
    ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();

    if (!Strings.isNullOrEmpty(startDate)) {
      builder.put(PROPERTY_START_DATE, startDate);
    }
    if (!Strings.isNullOrEmpty(endDate)) {
      builder.put(PROPERTY_END_DATE, endDate);
    }
    if (!Strings.isNullOrEmpty(statCategories)) {
      builder.put(PROPERTY_END_DATE, statCategories);
    }
    return builder.build();
  }

  /**
   * Client authentication way
   */
  public SendGridAuthType getAuthType() {
    switch (authType) {
      case "api":
        return SendGridAuthType.API;
      case "basic":
        return SendGridAuthType.BASIC;
      default:
        throw new IllegalArgumentException(String.format("Authentication using '%s' is not supported", authType));
    }
  }

  /**
   * Retrieves Api Key
   */
  public String getSendGridApiKey() {
    return sendGridApiKey;
  }

  /**
   * Retrieves username
   */
  public String getAuthUserName() {
    return authUserName;
  }

  /**
   * Retrieves password
   */
  public String getAuthPassword() {
    return authPassword;
  }

  @Nullable
  public String getStartDate() {
    return startDate;
  }

  @Nullable
  public String getEndDate() {
    return endDate;
  }

  public List<String> getDataSourceTypes() {
    if (!Strings.isNullOrEmpty(dataSourceTypes)) {
      return Arrays.asList(dataSourceTypes.split(","));
    }
    return Collections.emptyList();
  }
}
