# Sendgrid Post-run Action


Description
-----------
The Sendgrid post-action can be used to send an email at the end of a pipeline run. You can configure it to send an 
email when the pipeline completes, succeeds or fails. Uses the Sendgrid service to send emails, and requires you to 
sign up for a Sendgrid account.


Use Case
--------
This action can be used when you want to send an email at the end of a pipeline run. For example, you may want to 
configure a pipeline so that an email is sent whenever the run failed for any reason.


Properties
----------
**Run Condition:** When to run the action. Must be 'completion', 'success', or 'failure'. Defaults to 'completion'.
If set to 'completion', the action will be executed regardless of whether the pipeline run succeeded or failed.
If set to 'success', the action will only be executed if the pipeline run succeeded.
If set to 'failure', the action will only be executed if the pipeline run failed.

**API Key**: The SendGrid API Key. After logging into your Sendgrid account, you can create a key in the API keys 
section of the Settings page.

**From:** The address to send the email from.

**To:** The address to send the email to.

**Subject:** The subject of the email.

**Content:** Optional content of the email. Defaults to empty.


Example
-------
This example sends an email from 'team-ops@example.com' to 'team-alerts@example.com' whenever a run fails:

    {
        "name": "Sendgrid",
        "type": "postaction",
        "properties": {
            "apiKey": "xyz",
            "from": "team-ops@example.com",
            "to": "team-alerts@example.com",
            "subject": "Pipeline Failure ${logicalStartTime(yyyy-MM-dd)}",
            "message": "The pipeline run failed.",
            "runCondition": "failure"
        }
    }
