ToPdfA Version 0.0.2-SNAPSHOT | cUrl command to call AEM Assembler service to convert PDF to PDF/A

NAME
====

**ToPdfA** —  Convert a given document to PDF/A using the options specified.

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services/AssemblerService/ToPdfA**

DESCRIPTION
===========

Convert a given document to PDF/A using the options specified.

Options
-------

**-u** *username***:***password*

:  Username and password for authentication on the AEM server.  On an out-of-the-box AEM installation, `-u admin:admin` should work.

**-k** | **--insecure**

:  When using HTTPS, this option makes curl skip the SSL verification step and proceed without validating the certificate.  it is required if the SSL certificate is a self-sign cert,

**-o** | **--output** *file*

:  Send the output to *file*.  This sends the response from AEM to a file. *Caution*: Even error responses are sent to the file, so the file you create *may not be a PDF*.  If you have trouble opening the PDF after executing this command, try opening it on a text editor.  You may find that an error occurred and the file contains the error response.  The text in error responses are usually not sufficient to debug, so if an error occurs while rendering, check the AEM server log (error.log) for details.

**-F "inDoc=@***localPdfPath***"**

:   Provides the input Pdf to be converted. *localPdfPath* is an absolute or relative path to supported file
type on the local machine. This parameter is required.

**-F "colorSpace=**[ **COATED_FOGRA27** | **JAPAN_COLOR_COATED** | **S_RGB** | **SWOP** ]**"**

:   Supported ColorSpace handling options. Options include "Coated FOGRA27", "Japan color coated", "sRGB", or "SWOP". This parameter is optional.

**-F "compliance=**[ **PDFA_1B** | **PDFA_2B** | **PDFA_3B** ]**"**

:   Supported compliance. Options include "PDF/A-1b", "PDF/A-2b", "PDF/A-3b". This parameter is optional.

**-F "logLevel=***logLevel***"**

:   Designates a String containing the log level. The log level can be set to one of the following values that 
have integer equivalents:  
OFF = positive infinity  
SEVERE = 1000  
WARNING = 900  
INFO = 800  
CONFIG = 700  
FINE = 500  
FINER = 400  
FINEST = 300  
ALL = negative infinity  
If the log level is not set, then it defaults to "INFO". For investigation of problems, it can be set to "FINE", "FINER" or "FINEST", but performance will be negatively affected in this mode. This parameter is optional.

**-F "metadataExtension=@***localXmlPath***"**

:   Adobe documentation merely states "the metadataSchemaExtensions". This parameter is optional.  This parameter can repeat.

**-F "optionalContent=**[ **ALL** | **VISIBLE** ]**"**

:   Possible OptionalContent handling options. Options include "All optional content will be converted into visible content", or "Visible optional content will be converted into content and non-visible will be removed". This parameter is optional.

**-F "resultLevel=**[ **DETAILED** | **PASS_FAIL** | **SUMMARY** ]**"**

:   Supported result levels. Options include "List all available info", "Pass/Fail - least verbose", "List the errors that occurred, but not their instances". This parameter is optional.

**-F "signatures=**[ **ARCHIVE_ALWAYS** | **ARCHIVE_AS_NEEDED** ]**"**

:   Supported Signature handling option. Options include "Always archive signature fields"and "Only archive signature fields if the save requires". This parameter is optional.

**-F "removeInvalidXmlProperties=**[ **true** | **false** ]**"**

:   Whether to remove invalid XMP properties or not. This parameter is optional.

**-F "retainPdfFormState=**[ **true** | **false** ]**"**

:   Whether to retain Pdf form state. This parameter is optional.

**-F "verify=**[ **true** | **false** ]**"**

:   Adobe documentation doesn't make the purpose of this setting clear, it merely says "the verify to set". This parameter is optional.


EXAMPLES
====

`curl -u admin:admin --insecure -F inDoc=@Sample.pdf http://172.18.110.21:4502/services/AssemblerService/ToPdfA`

Converts a single PDF to PDF/A.  Returns an XML containing the conversion log, job log, and converted PDF (all base-64 encoded).

`curl -u admin:admin --insecure -F inDoc=@Sample.pdf  http://172.18.110.21:4502/services/AssemblerService/ToPdfA | xmlstarlet sel -t -v "/ToPdfAResult/PdfADocument" | base64 -d > result.pdf`

Converts a single PDF to PDF/A.  It extracts the PDF document out of the returned XML, base-64 decodes it and places the 
resulting output into result.pdf.  NOTE: You need to have xmlstarlet installed (easy on Ubuntu `apt install xmlstarlet`, 
but harder on Red Hat - you first need to `yum install epel-release` before doing `yum install xmlstarlet`).

BUGS
====

See GitHub Issues: <https://github.com/4PointSolutions/FluentFormsAPI/issues>

AUTHOR
======

4Point Solutions Ltd.

SEE ALSO
========

[cUrl Man Page](https://curl.se/docs/manpage.html)  
[AEM Assembler Service API](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/assembler/service/AssemblerService.html)  
[PDF Conversion Options](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/assembler/client/PDFAConversionOptionSpec.html) 
