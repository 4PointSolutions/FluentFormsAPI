package com._4point.aem.fluentforms.testing.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePdfArgs;

class MockOutputServiceTest {

	@Test
	void testCreateGeneratePdfOutputMock() throws Exception {
		String expectedResultString = "Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		MockOutputService underTest = MockOutputService.createGeneratePdfOutputMock(expectedResultDoc);
		
		PathOrUrl template = PathOrUrl.from("crx:/test/form");
		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
		
		Document result = underTest.generatePDFOutput()
						 .executeOn(template, data);
		
		assertEquals(result, expectedResultDoc);
		GeneratePdfArgs capturedArgs = underTest.getGeneratePdfArgs();
		assertEquals(data, capturedArgs.getData());
		assertEquals(template.getCrxUrl(), capturedArgs.getUrlOrFilename());
	}
//
//  The following code is commented out because the associates implementations have not yet been developed.
//  When the PrintedOutput and Batch APIs have been implemented, the code below will be a good starting 
//  point, however none of it has been tested so beware typos!
//
//	@Test
//	void testCreateGeneratePdfOutputBatchMock() throws Exception {
//		String expectedResultString = "Batch Test Result";
//		String expectedMetadataString = "Batch Test Metadata Result";
//		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
//		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
//		Document expectedMetadataResultDoc = mockDocumentFactory.create(expectedMetadataString.getBytes());
//		
//		BatchResult expectedResults = new BatchResult() {
//			
//			@Override
//			public Document getMetaDataDoc() {
//				return expectedMetadataResultDoc;
//			}
//			
//			@Override
//			public List<Document> getGeneratedDocs() {
//				return Arrays.asList(expectedResultDoc);
//			}
//		};
//		MockOutputService underTest = MockOutputService.createGeneratePdfOutputBatchMock(expectedResults);
//		
//		PathOrUrl template = PathOrUrl.from("crx:/test/form");
//		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
//		
//		Document result = underTest.generatePDFOutputBatch(templates, data, pdfOutputOptions, batchOptions)
//						 .executeOn(template, data);
//		
//		assertEquals(result, expectedResults);
//		GeneratePdfArgs capturedArgs = underTest.getGeneratePdfArgs();
//		assertEquals(data, capturedArgs.getData());
//		assertEquals(template.getCrxUrl(), capturedArgs.getUrlOrFilename());
//	}

//	@Test
//	void testCreateGeneratePrintedOutputMock() throws Exception {
//		String expectedResultString = "Test Result";
//		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
//		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
//		MockOutputService underTest = MockOutputService.createGeneratePrintedOutputMock(expectedResultDoc);
//		
//		PathOrUrl template = PathOrUrl.from("crx:/test/form");
//		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
//		
//		Document result = underTest.generatePrintedOutput()
//						 .executeOn(template, data);
//		
//		assertEquals(result, expectedResultDoc);
//		GeneratePdfArgs capturedArgs = underTest.getGeneratePdfArgs();
//		assertEquals(data, capturedArgs.getData());
//		assertEquals(template.getCrxUrl(), capturedArgs.getUrlOrFilename());
//	}

//	@Test
//	void testCreateGeneratePrintedOutputBatchMock() {
//		fail("Not yet implemented");
//	}

}
