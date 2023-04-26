AssembleDocuments Version 0.0.2-SNAPSHOT | cUrl command to call AEM Assembler service

NAME
====

**AssembleDocuments** —  Provides capabilities to perform various operations on PDFs, like 
assembling/disassembling, importing/exporting bookmarks, conversion/validation against PDF/A standards, etc.

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services/AssemblerService/AssembleDocuments**

DESCRIPTION
===========

Executes the DDX contained in the ddx option, returning an XML representation of the AssemblerResult object containing the result documents.

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

**-F "ddx=@***localDdxPath***"**

:   Provides the input DDX. *localDdxPath* is an absolute or relative path to supported file
type on the local machine. This parameter is required.

**-F "sourceDocumentMap.key=***keyString***"**

:   The DDX will specify documents by name, the `sourceDocumentMap.key` and `sourceDocumentMap.value`  parameters are used in pairs to supply the input documents referenced by the DDX.  This parameter may repeat any number of times and at least one is required.

**-F "sourceDocumentMap.value=@***localDocPath***"**

:   Provides a document that is associated with a `sourceDocuemtMap.key` parameter.  The first `sourceDocumentMap.value` is associated with the first `sourceDocumentMap.key`, the second value with the second key and so on.  One `sourceDocumentMap.value` must be supplied for each `sourceDocumentMap.key` parameter.

**-F "isFailOnError=**[ **true** | **false** ]**"**

:   Determines whether the DDX job should exit immediately when an Exception or other error occurs. 
A value of true means that the job will terminate upon an error or exception condition and throw the corresponding Throwable exception. A value of false means that the job will terminate the current DDX result block, store the Throwable exception in the output map, and attempt to execute any remaining result blocks. The default is true, meaning that the first error will terminate the DDX job immediately. This parameter is optional.

**-F "isTakeOwnerShip=**[ **true** | **false** ]**"**

:   Determines whether assembler can assume ownership of the document. A value of true means that assembler can assume ownership of the document and access the files directly instead of copying the input files to a temporary folder.A value of false means that assembler cannot assume ownership of the doucment and temporary files has to be created. The default is false. This parameter is optional.

**-F "defaultStyle=***stylingString***"**

:   Designates a default style to use when none is specified in the DDX document. This is a CSS2 style string 
in the format of `"property: value; property: value"`. For instance, 
`"font:<font-style> <font-weight> <font-size>/<line-height> <font-family-specifier>"`. 
Note that if the `<font-family-specifier>` contains spaces, it should be surrounded with single quotes. 
For example, `'Minion Pro'`. If no default is ever specified, a default of 
`"font: normal normal 12pt 'Minion Pro'"` is used. This parameter is optional.

**-F "firstBatesNumber=***integer***"**

:   Designates the first numeric value to use when the BatesNumber element does not contain a start attribute. This parameter is optional.

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

**-F "isValidatedOnly=**[ **true** | **false** ]**"**

:   Designates whether the DDX job is a validation-only test, which means that Assembler should validate the DDX but not execute it. A value of true halts processing of the DDX after the DDX has been parsed and verified to be in compliance with the schema and application validation rules. In this case the result map will be null, and the input map is allowed to be empty or null since the input data streams are not evaluated until actually used during job execution. The default is false, meaning that normal processing will be performed. This parameter is optional.


EXAMPLES
====

`curl -u admin:admin --insecure -F ddx=@Sample.ddx -F sourceDocumentMap.key=inDoc -F sourceDocumentMap.value=@Sampls.pdf http://172.18.110.21:4502/services/AssemblerService/AssembleDocuments`

Executes a DDX on a single input PDF and sends the result XML to stdout.

`curl -u admin:admin --insecure -F data=@SampleDDX.ddx -F sourceDocumentMap.key=inDoc -F sourceDocumentMap.value=@Sampls.pdf  http://172.18.110.21:4502/services/AssemblerService/AssembleDocuments | xmlstarlet sel -t -v "/assemblerResult/resultDocument/mergedDoc" | base64 -d > result.pdf`

Executes a DDX on a single input PDF, parses the result XML, base64 decodes and writes the result document to 
result.pdf.  NOTE: You need to have xmlstarlet installed (easy on Ubuntu `apt install xmlstarlet`, 
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
[Assembler Options](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/assembler/client/AssemblerOptionSpec.html) 
