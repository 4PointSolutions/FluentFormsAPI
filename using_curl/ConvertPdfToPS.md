ConvertPdf Version 0.0.2-SNAPSHOT | cUrl command to convert PDF to PostScript

NAME
====

**ConvertPdf** —  Convert a given PDF to PostScript using the options specified.

SYNOPSIS
========

| **curl** \[**-u** *username***:***password*] \[**-k**|**--insecure**] \[**-o**|**--output** *file*] \[*options*] 
**http://***machine***:***port***/services/ConvertPdfService/ToPS**

DESCRIPTION
===========

Convert a given PDF to PostScript using the options specified.

Options
-------

**-u** *username***:***password*

:  Username and password for authentication on the AEM server.  On an out-of-the-box AEM installation, `-u admin:admin` should work.

**-k** | **--insecure**

:  When using HTTPS, this option makes curl skip the SSL verification step and proceed without validating the certificate.  it is required if the SSL certificate is a self-sign cert,

**-o** | **--output** *file*

:  Send the output to *file*.  This sends the response from AEM to a file. *Caution*: Even error responses are sent to the file, so the file you create *may not be a PDF*.  If you have trouble opening the PDF after executing this command, try opening it on a text editor.  You may find that an error occurred and the file contains the error response.  The text in error responses are usually not sufficient to debug, so if an error occurs while rendering, check the AEM server log (error.log) for details.

**-F "inPdfDoc=@***localPdfPath***"**

:   Provides the input Pdf to be converted. *localPdfPath* is an absolute or relative path to supported file
type on the local machine. This parameter is required.

**-F "toPSOptionsSpec.allowBinaryContent=**[ **true** | **false** ]**"**

:   Sets if the PostScript file is created in a binary format. This parameter is optional.

**-F "toPSOptionsSpec.bleedMarks=**[ **true** | **false** ]**"**

:   This option indicates that a mark in each of the four corners of the bleed box is present. This parameter is optional.

**-F "toPSOptionsSpec.color=**[ **composite** | **compositeGrey** ]**"**

:   Sets the Color object that represents the composite color. Options include "composite" which represents a composite color value, or "Japan color coated" which represents a compositeGray color value (i.e. produces a black and white image). This parameter is optional.

**-F "toPSOptionsSpec.colorBars=**[ **true** | **false** ]**"**

:   This option places a color bar at the top of the page, outside of the crop area. The color bar shows a single box for each spot or process color in the document. Spot colors converted to process colors are represented by process colors. This parameter is optional.

**-F "toPSOptionsSpec.convertTrueTypeToType1=**[ **true** | **false** ]**"**

:   This option converts TrueType fonts to Type 1 fonts in the resulting PostScript file. Some older PostScript output devices may require this conversion. This parameter is optional.

**-F "toPSOptionsSpec.emitCIDFontType2=**[ **true** | **false** ]**"**

:   This option preserves the hinting information in the original font when printing. If this option is not set, then CIDFontType2 fonts are converted to CIDFontType0 fonts, which are compatible with a wider range of printers. This parameter is optional.

**-F "toPSOptionsSpec.emitPSFormsObjects=**[ **true** | **false** ]**"**

:   Emit PostScript form objects for Form XObjects within the input PDF document. This may reduce the overall size of the print job, but it will increase the printer memory that is used. Form XObjects are used to create a single description for complex objects that can appear many times in a single document, like background images, for example. This parameter is optional.

**-F "toPSOptionsSpec.expandToFit=**[ **true** | **false** ]**"**

:   This option expands the size of the resulting PostScript file to fit the page. When setting this option, you must also set the PageSize option using the setPageSize method. This parameter is optional.

**-F "toPSOptionsSpec.fontInclusion=**[ **embeddedAndReferencedFonts** | **embeddedFonts** | **none** ]**"**

:   Indicates whether/which fonts should be embedded in the resulting PostScript. Options include "embeddedAndReferencedFonts" which indicates that both embedded as well referenced fonts in the input PDF document should be embedded in the output PostScript file, "embeddedFonts" which indicates that only fonts that are embedded in the input PDF document should be embedded in the output PostScript file, "none" which indicates that fonts should not embedded in the resulting PostScript file. This parameter is optional.

**-F "toPSOptionsSpec.includeComments=**[ **true** | **false** ]**"**

:   This option preserves the appearance of comments in the resulting PostScript file. The information located in the comment is not present in the resulting PostScript file, only the appearance of the comment. This parameter is optional.

**-F "toPSOptionsSpec.legacyToSimplePSFlag=**[ **true** | **false** ]**"**

:   This option supports conversion of interactive documents to postscript. This parameter is optional.

**-F "toPSOptionsSpec.lineWeight=**[ **point125** | **point25** | **point5** ]**"**

:   Sets the weight of the lines in the resulting PostScript file, which are used to enhance drawing readability. This enumeration value is used for lines for trim, bleed, and registration marks. Options include "point125" which represents a line weight of 0.125, "point25" which represents a line weight of 0.25, and "point5" which represents a line weight of 0.50. This parameter is optional.

**-F "toPSOptionsSpec.pageInformation=**[ **true** | **false** ]**"**

:   This option outputs page information outside of the crop area of the page. This parameter is optional.

**-F "toPSOptionsSpec.pageRange=***pageRange***"**

:   Option string that specifies the page range value. For example, 1: "2-6", Example 2: "1,3,5-9,21-30". Passing an empty value results all pages used witin the page range. This parameter is optional.

**-F "toPSOptionsSpec.pageSize=**[ **A2** | **A3** | **A4** | **A5** | **Custom** | **DetermineAutomatically** | **Envelope** | **Executive** | **Folio** | **Legal** | **Letter** | **Tabloid** ]**"**

:   Sets the page size to an enumeration value that specifies the size of pages in the resulting PostScript file. This parameter is optional.

**-F "toPSOptionsSpec.pageSizeHeight=***pageHeight***"**

:   Option string that sets the page height of the resulting PostScript file.. This parameter is optional.

**-F "toPSOptionsSpec.pageSizeWidth=***pageWidth***"**

:   Option string that sets the page width of the resulting PostScript file. This parameter is optional.

**-F "toPSOptionsSpec.psLevel=**[ **LEVEL_2** | **LEVEL_3** ]**"**

:   Sets the PostScript language level of the resulting PostScript file. This parameter is optional.

**-F "toPSOptionsSpec.registrationMarks=**[ **true** | **false** ]**"**

:   This option places registration marks outside of the crop area of the page. This parameter is optional.

**-F "toPSOptionsSpec.reverse=**[ **true** | **false** ]**"**

:   This option sets the Reverse Option (this is all the Adobe documentation says). This parameter is optional.

**-F "toPSOptionsSpec.rotateAndCenter=**[ **true** | **false** ]**"**

:   This option centers the page content in the resulting PostScript document. This parameter is optional.

**-F "toPSOptionsSpec.shrinkToFit=**[ **true** | **false** ]**"**

:   This option shrinks the size of the resulting PostScript file to fit the page. When setting this option, you must also set the PageSize option using the setPageSize method. This parameter is optional.

**-F "toPSOptionsSpec.style=**[ **Default** | **Illustrator** | **IllustratorJ** | **InDesignJ1** | **InDesignJ2** | **QuarkXPress** ]**"**

:   This option specifies the style of printer marks to create. This parameter is optional.

**-F "toPSOptionsSpec.trimMarks=**[ **true** | **false** ]**"**

:   This option indicates that marks are placed in each of the four corners of the trim box in the resulting PostScript file. This parameter is optional.

**-F "toPSOptionsSpec.useMaxJPEGImageResolution=**[ **true** | **false** ]**"**

:   This option indicates that the highest available resolution for printing JPEG 2000 images is used. If you leave do not set this option, the pixels per inch value (from the Line Art and Text Resolution field in the Flattener Preview dialog) multipled by the value 2 is used as the rasterization resolution for JPEG2000 images. For example, if 72 ppi is selected (in the Line Art and Text Resolution field) and this option is not set, JPEG2000 images are rasterized into 144 ppi images. This parameter is optional.

EXAMPLES
====

`curl -u admin:admin -F "inPdfDoc=@AemOutput_pdf.pdf" -F "toPSOptionsSpec.fontInclusion=embeddedAndReferencedFonts" -o AemOutput_pdf.ps http://localhost:4502/services/ConvertPdf/ToPS`

Converts a PDF file into a PostScript file.

BUGS
====

See GitHub Issues: <https://github.com/4PointSolutions/FluentFormsAPI/issues>

AUTHOR
======

4Point Solutions Ltd.

SEE ALSO
========

[cUrl Man Page](https://curl.se/docs/manpage.html)  
[AEM Convert Pdf Service API](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/cpdf/api/ConvertPdfService.html)  
[To PS Options](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/com/adobe/fd/cpdf/api/ToPSOptionsSpec.html) 
