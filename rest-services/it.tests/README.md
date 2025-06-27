# FluentForms REST Services Integration Test Project

This maven project contains the Integration tests that run against (and require) a real AEM instance.

Some tests are tagged because they require the ream AEM instance to be configured in a specific way.  These tags are:

`requiresPdfG` - Requires PDFG to be configured

`requiresRe` - Requires a Reader Extensions credential to be configured.

You can exclude these tags in order for the tests to run successfully when the AEM instance is not configured to meet these requirements.