# SendGrid batch source

Description
-----------
Plugin fetches data from SendGrid. SendGrid is a cloud-based service that assists businesses with email delivery. For 
the end user SendGrid provides information about existing Marketing Campaigns, Email Analytics, Bounces, Spam Reports.
 
Properties
----------
### General

**Reference Name:** Name used to uniquely identify this source for lineage, annotating metadata, etc.

**Authentication type:** The way, how user would like to be authenticated to the SendGrid account

**API Key:** The SendGrid API Key taken from the SendGrid account

**Username:** Login name for the SendGrid account

**Password:** Password for the SendGrid account

**Data Source Types:** List of data source groups

Available:
- Marketing Campaigns Fields
- Stats Fields
- Suppressions Fields

**Data Source:**  SendGrid source object

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
  
**Data Source Fields:** The list of fields available for the retrieval

- *Automation:* id, name, status, type, message_count, created_at, updated_at, live_at
- *SingleSends:* id, name, status, created_at, updated_at,
- *Senders:* id, nickname, address, address_2, city, country, state, zip, locked, created_at, updated_at, from(email, name), verified(status, reason), reply_to(name, email)
- *Contacts:* id, first_name, last_name, list_ids, created_at, updated_at, email, Segments, id, name, parent_list_id, created_at, updated_at, sample_updated_at, contacts_count
- *GlobalStats:* date ,blocks ,bounce_drops ,bounces ,clicks ,deferred ,invalid_emails ,opens ,processed ,requests ,spam_report_drops ,spam_reports ,unique_clicks ,unique_opens ,unsubscribe_drops ,unsubscribes
- *CategoryStats:* name, type, date, blocks, bounce_drops, bounces, clicks, deferred, delivered, invalid_emails, opens, processed, requests, spam_report_drops, spam_reports, unique_clicks, unique_opens, unsubscribe_drops, unsubscribes
- *AdvancedStats:* name, type, date, clicks, opens, unique_clicks, unique_opens
- *Bounces:* created, email, reason, status
- *GroupUnsubscribes:* id ,name ,description ,is_default ,last_email_send_at ,unsubscribes

**Start Date:** The date in format YYYY-MM-DD, starting from which the data is requested

**End Date:**  The date in format YYYY-MM-DD, the end date for the requested data



