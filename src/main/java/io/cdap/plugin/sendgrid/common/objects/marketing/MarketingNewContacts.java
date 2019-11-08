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
package io.cdap.plugin.sendgrid.common.objects.marketing;

import com.google.gson.annotations.SerializedName;
import io.cdap.plugin.sendgrid.common.helpers.BaseObject;
import io.cdap.plugin.sendgrid.common.helpers.IBaseObject;
import io.cdap.plugin.sendgrid.common.helpers.ObjectDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Object for creating new SendGrid contacts
 */
@ObjectDefinition(APIUrl = "marketing/contacts")
public class MarketingNewContacts extends BaseObject implements IBaseObject {
    @SerializedName("list_ids")
    private List<String> listIds;

    @SerializedName("contacts")
    private List<MarketingNewContact> contacts;

    public MarketingNewContacts(List<String> csvLines) {
        listIds = new ArrayList<>();
        contacts = csvLines.stream().map(MarketingNewContact::fromCSVLine).collect(Collectors.toList());
    }

    public MarketingNewContacts(String csvLines) {
        this(Arrays.asList(csvLines.split(System.lineSeparator())));
    }

    @Override
    public Map<String, Object> asMap() {
        return null;
    }

    public List<MarketingNewContact> getContacts() {
        return contacts;
    }
}
