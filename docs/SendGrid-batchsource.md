# SendGrid batch source

Description
-----------
This plugin used to query SendGrid v3 API.

Properties
----------
### General

**Reference Name:** Name used to uniquely identify this source for lineage, annotating metadata, etc.

**Authentication type:** The way, how user would like to be authenticated to the SendGrid acoount

**API Key:** The SendGrid API Key taken from the SendGrid account

**Username:** Login name for SendGrid

**Password:** Login password for the username specified above

**Data Source Types:** List of data source groups

Available:
- Marketing Campaigns Fields
- Stats Fields
- Suppressions Fields

**Data Source:** One of the above sources picked from list

Available:
- Marketing Campaigns Fields
  - Automation
  - Single Sends
  - Senders
  - Contacts
  - Segments
- Stats
  - Global Stats
  - Category Stats
  - Advanced Stats
- Suppressions
  - Bounces
  - Global Unsubscribes
  - Group Unsubscribes
**Data Source Fields:** 

**Start Date:** The date in format YYYY-MM-DD, starting from which the data is requested

**End Date:**  The date in format YYYY-MM-DD, the end date for the requested data



