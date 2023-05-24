RenderPdfForm Version 0.0.2-SNAPSHOT | cUrl command to render an interative PDF Form using fluentforms.rest-services

NAME
====

**RenderPdfForm** — Renders an interactive PDF from an XDP Template (optionally with data)

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services/FormsService/RenderPdfForm**

DESCRIPTION
===========

Renders an interactive PDF form using AEM's Forms Service.


Options
-------

**-u** *username***:***password*

:  Username and password for authentication on the AEM server.  On an out-of-the-box AEM installation, `-u admin:admin` should work.

**-k** | **--insecure**

:  When using HTTPS, this option makes curl skip the SSL verification step and proceed without validating the certificate.  it is required if the SSL certificate is a self-sign cert,

**-o** | **--output** *file*

:  Send the output to *file*.  This sends the response from AEM to a file. *Caution*: Even error responses are sent to the file, so the file you create *may not be a PDF*.  If you have trouble opening the PDF after executing this command, try opening it on a text editor.  You may find that an error occurred and the file contains the error response.  The text in error responses are usually not sufficient to debug, so if an error occurs while rendering, check the AEM server log (error.log) for details.

**-F "template=***serverTemplatePath***;type=text/plain"** |  
**-F "template=@***localTemplatePath***;type=application/vnd.adobe.xdp+xml"** |  
**-F "template=@***localTemplatePath***;type=application/pdf"** 

:   Designates the template that will be used. This is a mandatory parameter.  

*serverTemplatePath* is the location of the template on the
AEM server.  It can be an absolute file path, a relative file path (relative to the contentRoot), an http/https URL, or a crx url.

*localTemplatePath* is an absolute or relative path to a XDP or PDF template on the local machine.  Links in the XDP/PDF (images or fragments) will not work with this approach unless those links are available on the server (relative to the contentRoot).

The same parameter (template) is used for the three different types of templates (server-based, local XDP, and local PDF).  Because of this, the type parameter must be used to set the content-type for this parameter.  The server-side code uses this content-type to determine what type of template is being supplied. 

**-F "data=@***localDataPath***"**

:   Provides the data file to be used. *localDataPath* is an absolute or relative path to a XDP or PDF template on the local machine. This parameter is optional.

**-F "renderOptions.acrobatVersion=**[ **Acrobat_10** | **Acrobat_10_1** | **Acrobat_11** ]**"**

:   Designates which version of Acrobat should be targeted. Acrobat_11 is the default. This parameter is optional.

**-F "renderOptions.cacheStrategy=**[ **AGGRESSIVE** | **CONSERVATIVE** | **NONE** ]**"**

:   Designates the cache strategy. It defaults to AGGRESSIVE. This parameter is optional.

**-F "renderOptions.contentRoot=***serverPath***"**

:   Designates the root location of the form. If a relative path is provided for the template, then it is 
relative to this location. This parameter is optional.

**-F "renderOptions.debugDir=***serverPath***"**

:   Designates directory on the server where debug information will be placed. This parameter is optional.

**-F "renderOptions.embedFonts=**[ **true** | **false** ]**"**

:   Determines whether fonts used in the form are embedded in the PDF or not. This parameter is optional.

**-F "renderOptions.locale=***languageString***"**

:   Sets the language where language is an IETF BCP 47 language tag string. This parameter is optional.

**-F "renderOptions.renderAtClient=**[ **YES** | **NO** | **AUTO** ]**"**

:   Designates whether the PDF form's PDF layer is rendered on the server by AEM or by Acrobat on the client machine. This parameter is optional.

**-F "renderOptions.submitUrl=***submissionUrl***"**

:   Designates the URL where the form data will be sent when the user submits the PDF. Can be specified multiple
times (the Adobe API allows for multiple URLs), but typically is only used once. (We have no idea what happens if you specify multiple URLs). This parameter is optional.

**-F "renderOptions.taggedPdf=**[ **true** | **false** ]**"**

:   Designates whether the PDF is rendered with or without tagging. This parameter is optional.

**-F "renderOptions.xci=@***localPath***"**

:   Provides a custom XCI that is merged with the default XCI. This means that the custom XCI can be "sparse" (i.e. it only includes the options you wish to override from the default XCI). *localPath* is a path on the 
client machine to the custom XCI. This parameter is optional.


EXAMPLES
====

`curl -u admin:admin --insecure -F "template=sample-forms/sample.xdp;type=text/plain" -F "data=@sample_data.xml" -F "renderOptions.contentRoot=crx:/content/dam/formsanddocuments" -o sample_pdf.pdf https://localhost:4502/services/FormsService/RenderPdfForm`

Renders an interactive PDF using sample.xdp which is located in a sample-forms directory under "Forms And Documents" within CRX using a local data file (sample_data.xml).

BUGS
====

See GitHub Issues: <https://github.com/4PointSolutions/FluentFormsAPI/issues>

AUTHOR
======

4Point Solutions Ltd.

SEE ALSO
========

[cUrl Man Page](https://curl.se/docs/manpage.html)  
[AEM Forms Service API](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/forms/api/FormsService.html)  
[PDF Form Render Options](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/forms/api/PDFFormRenderOptions.html)