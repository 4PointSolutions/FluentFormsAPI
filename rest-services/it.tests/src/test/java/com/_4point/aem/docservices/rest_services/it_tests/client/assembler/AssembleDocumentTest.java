package com._4point.aem.docservices.rest_services.it_tests.client.assembler;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.ACTUAL_RESULTS_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DDX;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;

public class AssembleDocumentTest {

private static final boolean SAVE_RESULTS = true;

	private AssemblerService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		RestServicesDocAssemblerServiceAdapter adapter = RestServicesDocAssemblerServiceAdapter.builder()
														.machineName(TEST_MACHINE_NAME)
														.port(TEST_MACHINE_PORT)
														.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
														.useSsl(false)
														.build();

		underTest = new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test importData() Happy Path.")
	void testInvoke() throws Exception {
       
        Document ddx = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_DDX.toFile());
		Document pdf = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_PDF.toFile());
		Map<String, Object> sourceDocuments = new HashMap<String, Object>();
		sourceDocuments.put("File0.pdf", pdf);
		sourceDocuments.put("File1.pdf", pdf);
		
		AssemblerOptionsSpec assemblerOptionSpec = null;

		AssemblerResult assemblerResult = underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec);;
		          
		Map<String, Document> mergedDoc = assemblerResult.getDocuments();
		Document pdfResult = null;
        for(Entry<String, Document>doc : mergedDoc.entrySet()) {
        	 if(doc.getKey().equalsIgnoreCase("concatenatedPDF.pdf")) {
        		 pdfResult = (Document)doc.getValue();
        	 }
        }
		// Verify that all the results are correct.
		//assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		if (SAVE_RESULTS) {
			if(pdfResult != null) {
			IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("ImportDataClient_BytesResult.pdf")));
		}  else {
			System.out.println("Pdf is null");
		}
	   }
	}
}
