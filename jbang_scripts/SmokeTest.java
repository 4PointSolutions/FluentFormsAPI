///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,github=https://maven.pkg.github.com/4PointSolutions/*
//DEPS com._4point.aem:fluentforms.core:0.0.3-SNAPSHOT  com._4point.aem.docservices:rest-services.client:0.0.3-SNAPSHOT
//JAVA 21+

/*
 * 	This script uses the 4PointSolutions/FluentFormsAPI GitHub package repository.  GitHub requires a user to authenticate in order
 * 	to access a package repository.  In order for this script to work, you need to have your personal credentials configured
 * 	in your local settings.xml file (found in you $HOME/.m2 directory).
 * 	
 *  Your settings.xml should look something like this:
 *  	<?xml version="1.0"?>
 *  	<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
 *  		<servers>
 *  			<server>
 *  				<id>github</id>
 *  				<username>Your GitHub Username goes here</username>
 *  				<password>Your Personal Access Token goes here</password>
 *  			</server>
 *  		</servers>
 *  	</settings>
 * 
 */
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com.adobe.fd.output.api.AcrobatVersion;

class SmokeTest {

    public static void main(String... args) {
		
		try {
			Path outLocation = Files.createTempFile("AemSmokeTest", ".pdf");
			
			// If AEM is not localhost:4502 or does not use default credentials, change the values below. 
			var adapter = RestServicesOutputServiceAdapter.builder()
					.machineName("localhost")
					.port(4502)
					.basicAuthentication("admin", "admin")
					.useSsl(false)
					.build();

			var underTest = new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
			
			
			var builder = underTest.generatePDFOutput()
					.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
					.setEmbedFonts(true)
					// Less used parameters are commented out which leaves them at default.
//				.setLinearizedPDF(true)
//				.setLocale(Locale.CANADA_FRENCH)
//				.setRetainPDFFormState(true)
//				.setRetainUnsignedSignatureFields(true)
					.setTaggedPDF(true);

			Document pdfResult =  builder.executeOn(SMOKE_TEST_FORM.getBytes(), SMOKE_TEST_DATA.getBytes());

			System.out.println("✅ SmokeTest passed - writing output to " + outLocation.toAbsolutePath());
			Files.copy(pdfResult.getInputStream(), outLocation, StandardCopyOption.REPLACE_EXISTING);
			
			if (!GraphicsEnvironment.isHeadless()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(outLocation.toAbsolutePath().toUri());
			} else {
				System.out.println("Bypassing Desktop Launch");
			}

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ SmokeTest failed");
			System.exit(-1);
		}
    }

    private static final String SMOKE_TEST_DATA = 
	    	"""
	    	<?xml version="1.0" encoding="UTF-8"?>
	    	<form1>
	    	   <SampleField>This is sample data that demonstrates that data was merged with the form.</SampleField>
	    	</form1>   		
	    	""";

    private static final String SMOKE_TEST_FORM = 
    		"""
    		<?xml version="1.0" encoding="UTF-8"?>
    		<?xfa generator="Designer_V6.5.19.20231117.1.148" APIVersion="3.6.23313.0"?>
    		<xdp:xdp xmlns:xdp="http://ns.adobe.com/xdp/" timeStamp="2024-09-16T13:50:19Z" uuid="79b538a1-6192-490d-b164-4afa39964215">
    		<template xmlns="http://www.xfa.org/schema/xfa-template/3.6/">
    		   <?formServer defaultPDFRenderFormat acrobat11.0dynamic?>
    		   <?formServer allowRenderCaching 0?>
    		   <?formServer formModel both?>
    		   <subform name="form1" layout="tb" locale="en_CA" restoreState="auto">
    		      <pageSet>
    		         <pageArea name="Page1" id="Page1">
    		            <contentArea x="0.25in" y="0.25in" w="576pt" h="756pt"/>
    		            <medium stock="default" short="612pt" long="792pt"/>
    		            <?templateDesigner expand 1?></pageArea>
    		         <?templateDesigner expand 1?></pageSet>
    		      <subform w="576pt" h="756pt">
    		         <draw name="Text1" y="6.35mm" x="6.35mm" w="193.675mm" h="19.05mm">
    		            <ui>
    		               <textEdit/>
    		            </ui>
    		            <value>
    		               <exData contentType="text/html">
    		                  <body xmlns="http://www.w3.org/1999/xhtml" xmlns:xfa="http://www.xfa.org/schema/xfa-data/1.0/"><p style="text-align:center;text-decoration:none;letter-spacing:0in">Sample Form</p></body>
    		               </exData>
    		            </value>
    		            <font size="48pt" typeface="Myriad Pro" baselineShift="0pt"/>
    		            <margin topInset="0.5mm" bottomInset="0.5mm" leftInset="0.5mm" rightInset="0.5mm"/>
    		            <para spaceAbove="0pt" spaceBelow="0pt" textIndent="0pt" marginLeft="0pt" marginRight="0pt"/>
    		         </draw>
    		         <field name="SampleField" y="34.925mm" x="6.35mm" w="193.675mm" h="9mm">
    		            <ui>
    		               <textEdit>
    		                  <border>
    		                     <edge/>
    		                  </border>
    		                  <margin/>
    		               </textEdit>
    		            </ui>
    		            <font typeface="Myriad Pro"/>
    		            <margin topInset="1mm" bottomInset="1mm" leftInset="1mm" rightInset="1mm"/>
    		            <para vAlign="middle"/>
    		            <caption reserve="25mm">
    		               <para vAlign="middle"/>
    		               <value>
    		                  <text>Sample Field</text>
    		               </value>
    		            </caption>
    		         </field>
    		         <?templateDesigner expand 1?></subform>
    		      <proto/>
    		      <desc>
    		         <text name="version">6.5.19.20231117.148</text>
    		         <text name="contact">support@4point.com</text>
    		         <text name="description">This form is used for performing a Smoke Test of an AEM instance.  It exercises the Document Services APIs.</text>
    		         <text name="creator">4Point Solutions</text>
    		         <text name="title">Smoke Test</text>
    		      </desc>
    		      <?templateDesigner expand 1?>
    		      <?templateDesigner Hyphenation excludeInitialCap:1, excludeAllCaps:1, wordCharCnt:7, remainCharCnt:3, pushCharCnt:3?>
    		      <?renderCache.subset "Myriad Pro" 0 0 ISO-8859-1 4 60 15 0001002700340035004200450046004A004D004E00500051005300550059 FSTadeilmoprtx?></subform>
    		   <?templateDesigner DefaultPreviewDynamic 1?>
    		   <?templateDesigner Grid show:1, snap:1, units:0, color:ff8080, origin:(0,0), interval:(125000,125000)?>
    		   <?templateDesigner Zoom 106?>
    		   <?templateDesigner WidowOrphanControl 0?>
    		   <?templateDesigner SavePDFWithLog 0?>
    		   <?templateDesigner FormTargetVersion 36?>
    		   <?templateDesigner DefaultLanguage JavaScript?>
    		   <?templateDesigner DefaultRunAt server?>
    		   <?acrobat JavaScript strictScoping?>
    		   <?PDFPrintOptions embedViewerPrefs 0?>
    		   <?PDFPrintOptions embedPrintOnFormOpen 0?>
    		   <?PDFPrintOptions scalingPrefs 0?>
    		   <?PDFPrintOptions enforceScalingPrefs 0?>
    		   <?PDFPrintOptions paperSource 0?>
    		   <?PDFPrintOptions duplexMode 0?>
    		   <?templateDesigner DefaultPreviewType print?>
    		   <?templateDesigner DefaultPreviewPagination simplex?>
    		   <?templateDesigner XDPPreviewFormat 19?>
    		   <?templateDesigner DefaultPreviewDataFileName .\\\\SmokeTestData.xml?>
    		   <?templateDesigner DefaultCaptionFontSettings face:Myriad Pro;size:10;weight:normal;style:normal?>
    		   <?templateDesigner DefaultValueFontSettings face:Myriad Pro;size:10;weight:normal;style:normal?>
    		   <?templateDesigner SaveTaggedPDF 0?>
    		   <?templateDesigner SavePDFWithEmbeddedFonts 0?>
    		   <?templateDesigner Rulers horizontal:1, vertical:1, guidelines:1, crosshairs:0?></template>
    		<config xmlns="http://www.xfa.org/schema/xci/3.0/">
    		   <agent name="designer">
    		      <!--  [0..n]  -->
    		      <destination>pdf</destination>
    		      <pdf>
    		         <!--  [0..n]  -->
    		         <fontInfo/>
    		      </pdf>
    		   </agent>
    		   <present>
    		      <!--  [0..n]  -->
    		      <pdf>
    		         <!--  [0..n]  -->
    		         <fontInfo>
    		            <embed>0</embed>
    		         </fontInfo>
    		         <tagged>0</tagged>
    		         <version>1.7</version>
    		         <adobeExtensionLevel>11</adobeExtensionLevel>
    		      </pdf>
    		      <xdp>
    		         <packets>*</packets>
    		      </xdp>
    		   </present>
    		</config>
    		<localeSet xmlns="http://www.xfa.org/schema/xfa-locale-set/2.7/">
    		   <locale name="en_CA" desc="English (Canada)">
    		      <calendarSymbols name="gregorian">
    		         <monthNames>
    		            <month>January</month>
    		            <month>February</month>
    		            <month>March</month>
    		            <month>April</month>
    		            <month>May</month>
    		            <month>June</month>
    		            <month>July</month>
    		            <month>August</month>
    		            <month>September</month>
    		            <month>October</month>
    		            <month>November</month>
    		            <month>December</month>
    		         </monthNames>
    		         <monthNames abbr="1">
    		            <month>Jan</month>
    		            <month>Feb</month>
    		            <month>Mar</month>
    		            <month>Apr</month>
    		            <month>May</month>
    		            <month>Jun</month>
    		            <month>Jul</month>
    		            <month>Aug</month>
    		            <month>Sep</month>
    		            <month>Oct</month>
    		            <month>Nov</month>
    		            <month>Dec</month>
    		         </monthNames>
    		         <dayNames>
    		            <day>Sunday</day>
    		            <day>Monday</day>
    		            <day>Tuesday</day>
    		            <day>Wednesday</day>
    		            <day>Thursday</day>
    		            <day>Friday</day>
    		            <day>Saturday</day>
    		         </dayNames>
    		         <dayNames abbr="1">
    		            <day>Sun</day>
    		            <day>Mon</day>
    		            <day>Tue</day>
    		            <day>Wed</day>
    		            <day>Thu</day>
    		            <day>Fri</day>
    		            <day>Sat</day>
    		         </dayNames>
    		         <meridiemNames>
    		            <meridiem>AM</meridiem>
    		            <meridiem>PM</meridiem>
    		         </meridiemNames>
    		         <eraNames>
    		            <era>BC</era>
    		            <era>AD</era>
    		         </eraNames>
    		      </calendarSymbols>
    		      <datePatterns>
    		         <datePattern name="full">EEEE, MMMM D, YYYY</datePattern>
    		         <datePattern name="long">MMMM D, YYYY</datePattern>
    		         <datePattern name="med">D-MMM-YY</datePattern>
    		         <datePattern name="short">DD/MM/YY</datePattern>
    		      </datePatterns>
    		      <timePatterns>
    		         <timePattern name="full">h:MM:SS A Z</timePattern>
    		         <timePattern name="long">h:MM:SS A Z</timePattern>
    		         <timePattern name="med">h:MM:SS A</timePattern>
    		         <timePattern name="short">h:MM A</timePattern>
    		      </timePatterns>
    		      <dateTimeSymbols>GyMdkHmsSEDFwWahKzZ</dateTimeSymbols>
    		      <numberPatterns>
    		         <numberPattern name="numeric">z,zz9.zzz</numberPattern>
    		         <numberPattern name="currency">$z,zz9.99</numberPattern>
    		         <numberPattern name="percent">z,zz9%</numberPattern>
    		      </numberPatterns>
    		      <numberSymbols>
    		         <numberSymbol name="decimal">.</numberSymbol>
    		         <numberSymbol name="grouping">,</numberSymbol>
    		         <numberSymbol name="percent">%</numberSymbol>
    		         <numberSymbol name="minus">-</numberSymbol>
    		         <numberSymbol name="zero">0</numberSymbol>
    		      </numberSymbols>
    		      <currencySymbols>
    		         <currencySymbol name="symbol">$</currencySymbol>
    		         <currencySymbol name="isoname">CAD</currencySymbol>
    		         <currencySymbol name="decimal">.</currencySymbol>
    		      </currencySymbols>
    		      <typefaces>
    		         <typeface name="Myriad Pro"/>
    		         <typeface name="Minion Pro"/>
    		         <typeface name="Courier Std"/>
    		         <typeface name="Adobe Pi Std"/>
    		         <typeface name="Adobe Hebrew"/>
    		         <typeface name="Adobe Arabic"/>
    		         <typeface name="Adobe Thai"/>
    		         <typeface name="Kozuka Gothic Pro-VI M"/>
    		         <typeface name="Kozuka Mincho Pro-VI R"/>
    		         <typeface name="Adobe Ming Std L"/>
    		         <typeface name="Adobe Song Std L"/>
    		         <typeface name="Adobe Myungjo Std M"/>
    		         <typeface name="Adobe Devanagari"/>
    		      </typefaces>
    		   </locale>
    		</localeSet>
    		<x:xmpmeta xmlns:x="adobe:ns:meta/" x:xmptk="Adobe XMP Core 9.0-c000 79.cca54b0, 2022/11/26-09:29:55        ">
    		   <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    		      <rdf:Description xmlns:xmp="http://ns.adobe.com/xap/1.0/" xmlns:pdfuaid="http://www.aiim.org/pdfua/ns/id/" xmlns:pdf="http://ns.adobe.com/pdf/1.3/" xmlns:xmpMM="http://ns.adobe.com/xap/1.0/mm/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:desc="http://ns.adobe.com/xfa/promoted-desc/" rdf:about="">
    		         <xmp:MetadataDate>2024-09-16T13:50:19Z</xmp:MetadataDate>
    		         <xmp:CreatorTool>Designer 6.5</xmp:CreatorTool>
    		         <pdfuaid:part>1</pdfuaid:part>
    		         <pdf:Producer>Designer 6.5</pdf:Producer>
    		         <xmpMM:DocumentID>uuid:79b538a1-6192-490d-b164-4afa39964215</xmpMM:DocumentID>
    		         <dc:description>
    		            <rdf:Alt>
    		               <rdf:li xml:lang="x-default">This form is used for performing a Smoke Test of an AEM instance.  It exercises the Document Services APIs.</rdf:li>
    		            </rdf:Alt>
    		         </dc:description>
    		         <dc:creator>
    		            <rdf:Seq>
    		               <rdf:li>4Point Solutions</rdf:li>
    		            </rdf:Seq>
    		         </dc:creator>
    		         <dc:title>
    		            <rdf:Alt>
    		               <rdf:li xml:lang="x-default">Smoke Test</rdf:li>
    		            </rdf:Alt>
    		         </dc:title>
    		         <desc:version rdf:parseType="Resource">
    		            <rdf:value>6.5.19.20231117.148</rdf:value>
    		            <desc:ref>/template/subform[1]</desc:ref>
    		         </desc:version>
    		         <desc:contact rdf:parseType="Resource">
    		            <rdf:value>support@4point.com</rdf:value>
    		            <desc:ref>/template/subform[1]</desc:ref>
    		         </desc:contact>
    		      </rdf:Description>
    		   </rdf:RDF>
    		</x:xmpmeta></xdp:xdp>
    		""";
}
