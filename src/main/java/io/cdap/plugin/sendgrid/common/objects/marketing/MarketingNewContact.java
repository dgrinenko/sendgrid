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

/**
 * Object for creating new SendGrid contacts
 */
public class MarketingNewContact {
    @SerializedName("address_line_1")
    private String addressLine1;

    @SerializedName("address_line_2")
    private String addressLine2;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("email")
    private String email;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("state_province_region")
    private String stateProvinceRegion;

    /**
     * Initializes class from csv line.
     *
     * Column format:
     *   email,first_name,last_name,address_line_1,address_line_2,city,state_province_region,postal_code,country
     */
    public MarketingNewContact(String csvLine) {
      String[] columns = csvLine.split(",");
      if (columns.length != 9) {
          throw new IllegalArgumentException(String.format("Invalid csv formatted line: '%s'", csvLine));
      }
      this.email = columns[0];
      this.firstName = columns[1];
      this.lastName = columns[2];
      this.addressLine1 = columns[3];
      this.addressLine2 = columns[4];
      this.city = columns[5];
      this.stateProvinceRegion = columns[6];
      this.postalCode = columns[7];
      this.country = columns[8];
    }

    public static MarketingNewContact fromCSVLine(String csv) {
      return new MarketingNewContact(csv);
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStateProvinceRegion() {
        return stateProvinceRegion;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }
}
