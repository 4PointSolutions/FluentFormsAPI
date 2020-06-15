package com._4point.aem.fluentforms.examples;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.Reference;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.factory.ServerFactory;
import com.adobe.fd.output.api.AcrobatVersion;

class OutputServiceExamples {

	@Reference
	private com.adobe.fd.output.api.OutputService adobeOutputService;

	private String sampleData = "<foo><bar>dfljlsdfsf</bar></foo>";
	private String sampleXci = "<foo><bar>dfljlsdfsf</bar></foo>";
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void demoGeneratePdfOutputMinimal() throws Exception {
		OutputService outputService = ServerFactory.createOutputService(adobeOutputService);
		
		// 
		Document data = ServerFactory.getDefaultDocumentFactory().create(sampleData.getBytes());
		
		Document result = outputService.generatePDFOutput()
								.executeOn(Paths.get("foo/bar.xdp"), data );
		
		byte[] pdfBytes = result.getInlineData();	// you can do either of these but not both.
//		InputStream pdfStream = result.getInputStream();
	}

	@Test
	void demoGeneratePdfOutputMaximal() throws Exception {
		OutputService outputService = ServerFactory.createOutputService(adobeOutputService);
		
		// 
		DocumentFactory docFactory = ServerFactory.getDefaultDocumentFactory();
		Document data = docFactory.create(sampleData.getBytes());
		
		Document result = outputService.generatePDFOutput()
								.setContentRoot(Paths.get("Adobe", "forms"))
								.setAcrobatVersion(AcrobatVersion.Acrobat_11)
								.setDebugDir(Paths.get("Adobe", "debugDir"))
								.setEmbedFonts(true)
								.setLinearizedPDF(true)
								.setLocale(Locale.CANADA_FRENCH)
								.setRetainPDFFormState(false)
								.setRetainUnsignedSignatureFields(true)
								.setTaggedPDF(true)
								.setXci(docFactory.create(sampleXci.getBytes()))
								.executeOn(Paths.get("foo", "bar.xdp"), data );
		
//		byte[] pdfBytes = result.getInlineData();	// you can do either of these but not both.
		InputStream pdfStream = result.getInputStream();
	}

	@Test
	void demoGeneratePdfOutputTypical() throws Exception {
		OutputService outputService = ServerFactory.createOutputService(adobeOutputService);
		
		// 
		Document data = ServerFactory.getDefaultDocumentFactory().create(sampleData.getBytes());
		
		Document result = outputService.generatePDFOutput()
								.setContentRoot(Paths.get("Adobe", "forms"))
								.setTaggedPDF(true)
								.executeOn(Paths.get("foo/bar.xdp"), data );
		
		byte[] pdfBytes = result.getInlineData();	// you can do either of these but not both.
//		InputStream pdfStream = result.getInputStream();
	}

	@Test
	void demoGeneratePdfOutputTypical2() throws Exception {
		OutputService outputService = ServerFactory.createOutputService(adobeOutputService);
		
		// 
		Document data = ServerFactory.getDefaultDocumentFactory().create(sampleData.getBytes());
		
		Document result = outputService.generatePDFOutput()
								.setContentRoot(PathOrUrl.from("crx:/content/dam/formsanddocuments/"))
								.setTaggedPDF(true)
								.executeOn(Paths.get("foo/bar.xdp"), data );
		
//		byte[] pdfBytes = result.getInlineData();	// you can do either of these but not both.
		InputStream pdfStream = result.getInputStream();
	}


}
