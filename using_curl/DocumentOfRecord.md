DocumentOfRecord Version 0.0.2-SNAPSHOT | cUrl command to render a non-interactive PDF Document of Record using fluentforms.rest-services

NAME
====

**DocumentOfRecord** — Renders a non-interactive PDF from an Adaptive Form Template (optionally with data)

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services//DorService/Generate**

DESCRIPTION
===========

Renders a non-interactive PDF from an Adaptive Form using AEM's Document of Record Service.


Options
-------

**-u** *username***:***password*

:  Username and password for authentication on the AEM server.  On an out-of-the-box AEM installation, `-u admin:admin` should work.

**-k** | **--insecure**

:  When using HTTPS, this option makes curl skip the SSL verification step and proceed without validating the certificate.  it is required if the SSL certificate is a self-sign cert,

**-o** | **--output** *file*

:  Send the output to *file*.  This sends the response from AEM to a file. *Caution*: Even error responses are sent to the file, so the file you create *may not be a PDF*.  If you have trouble opening the PDF after executing this command, try opening it on a text editor.  You may find that an error occurred and the file contains the error response.  The text in error responses are usually not sufficient to debug, so if an error occurs while rendering, check the AEM server log (error.log) for details.

**-F "template=***serverTemplatePath***"**  

:   Designates the template that will be used. This is a mandatory parameter.  

*serverTemplatePath* is the location of the Adaptive Form template in CRX on the
AEM server.  It is a relative path (relative to FormsAndDocuments folder in CRX).


**-F "data=@***localDataPath***"**

:   Provides the data file to be used. *localDataPath* is an absolute or relative path to a XDP or PDF template on the local machine. This parameter is optional.

**-F "locale=***languageString***"**

:   Sets the language where language is an IETF BCP 47 language tag string. This parameter is optional.

**-F "attachment=@***localPath***"**

:   Provides an attachment that will be attached to the resulting document of record. 
This parameter may be repeated multiple times to attach multiple files.
*localPath* is a path on the client machine to the custom XCI. This parameter is optional.


EXAMPLES
====

`curl -u admin:admin --insecure -F "template=sample-forms/sample_form" -F "data=@sample_data.xml" -o sample_pdf.pdf https://localhost:4502/services//DorService/Generate`

Renders a non-interactive PDF using Adaptive Form called sample_form which is located in a sample-forms directory under "Forms And Documents" within CRX using a local data file (sample_data.xml).

BUGS
====

See GitHub Issues: <https://github.com/4PointSolutions/FluentFormsAPI/issues>

AUTHOR
======

4Point Solutions Ltd.

SEE ALSO
========

[cUrl Man Page](https://curl.se/docs/manpage.html)  
[AEM DoR Service API](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/aemds/guide/addon/dor/DoRService.html)  
[AEM DoR Options](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/aemds/guide/addon/dor/DoROptions.html)