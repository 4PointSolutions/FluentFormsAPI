CreatePdf Version 0.0.2-SNAPSHOT | cUrl command to render a printed form using fluentforms.rest-services

NAME
====

**CreatePdf** — Creates Adobe PDF from supported file types.

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services/GeneratePDFService/CreatePdf**

DESCRIPTION
===========

Creates Adobe PDF from supported file types. The service takes in a file and converts it to PDF. Supported file 
formats are msword, mspowerpoint, msexcel, msproject, msvisio, publisher, autocad. In addition to these, any third 
party generic pdf generating application type can be plugged into it.

The output from this service is an XML document that contains the base64-encoded PDF and a base64-encoded 
conversion log.

**NOTE** This service was originally built by a non-4Point contributor.  Not all the parameters have been
thoroughly tested.  The examples in the EXAMPLES section that use the required parameters have been tested and
they work.  If problems are encountered with the optional parameters, please log an issue in GitHub.

Options
-------

**-u** *username***:***password*

:  Username and password for authentication on the AEM server.  On an out-of-the-box AEM installation, `-u admin:admin` should work.

**-k** | **--insecure**

:  When using HTTPS, this option makes curl skip the SSL verification step and proceed without validating the certificate.  it is required if the SSL certificate is a self-sign cert,

**-o** | **--output** *file*

:  Send the output to *file*.  This sends the response from AEM to a file. *Caution*: Even error responses are sent to the file, so the file you create *may not be a PDF*.  If you have trouble opening the PDF after executing this command, try opening it on a text editor.  You may find that an error occurred and the file contains the error response.  The text in error responses are usually not sufficient to debug, so if an error occurs while rendering, check the AEM server log (error.log) for details.

**-F "data=@***localDataPath***"**

:   Provides the input file to be converted. *localDataPath* is an absolute or relative path to supported file
type on the local machine. This parameter is required.

**-F "fileExtension=***extension***"**

:   File extension of the document that needs to be converted. It must match one of the supported file type extensions.  This parameter is required.

**-F "fileTypeSettings=***fileTypeSettings***"**

:   Determines the file type settings. The Adobe documentation is unclear on how this is setting is used.  This parameter is optional.

**-F "pdfSettings=**[ **High_Quality_Print** | **PDFA1b_2005_RGB**  | **PDFA1b_2005_CMYK** | **PDFX1a_2001** | **PDFX3_2002**  | **Press_Quality** | **Smallest_File_Size** ]**"**

:   Determines the PDF settings that need to be applied to the output. This parameter is optional.

**-F "securitySettings=**[ **No_Security** | **Password_Security**  | **Certificate_Security** | **Adobe_Policy_Server** ]**"**

:   Determines the security settings that need to be applied to the output. This parameter is optional.

**-F "settingDoc=@***localPath***"**

:   Designates a file on the local machine that contains settings that need to be applied while generating the 
pdf(like optimization for web view) and also the settings that are applied once the pdf is created 
(InitialView, Security). This parameter is optional.

**-F "xmpDoc=@***localPath***"**

:   Designates a file on the local machine that contains metadata information that will be applied to 
the generated pdf. This parameter is optional.


EXAMPLES
====

`curl -u admin:admin --insecure -F data=@SampleSpreadsheet.xslx -F fileExtension=xslx http://172.18.110.21:4502/services/GeneratePDFService/CreatePDF`

Renders a pdf version of the spreadsheet (SampleSpreadsheet.xslx) which is located on the local machine.  The 
XML output is written to stdout.

`curl -u admin:admin --insecure -F data=@SampleForm.docx -F fileExtension=docx http://172.18.110.21:4502/services/GeneratePDFService/CreatePDF | xmlstarlet sel -t -v "/createPDFResult/createdDoc/@createdDocValue" result.xml | base64 -d > result.pdf`

Renders a pdf version of a word document (SampleSpreadsheet.xslx) which is located on the local machine. 
It then parses the resulting XML, extracts the created document, base64 decodes it and writes it to 
result.pdf.  NOTE: You need to have xmlstarlet installed (easy on Ubuntu `apt install xmlstarlet`, but 
harder on Red Hat - you first need to `yum install epel-release` before doing `yum install xmlstarlet`).

BUGS
====

See GitHub Issues: <https://github.com/4PointSolutions/FluentFormsAPI/issues>

AUTHOR
======

4Point Solutions Ltd.

SEE ALSO
========

[cUrl Man Page](https://curl.se/docs/manpage.html)  
[AEM Generate PDF Service API](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/pdfg/service/api/GeneratePDFService.html)  
