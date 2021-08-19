package com._4point.aem.fluentforms.testing.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

class MockPdfUtilityServiceTest {

	@Test
	void testCreateCloneMock() throws Exception {
		String expectedResultString = "Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		final MockPdfUtilityService underTest = MockPdfUtilityService.createCloneMock(expectedResultDoc);
		
		Document inputDoc = mockDocumentFactory.create("Input Doc".getBytes());
		final Document result = underTest.clone(inputDoc);
		
		assertSame(inputDoc, underTest.getInputDoc());
		assertSame(expectedResultDoc, result);
	}

	@Test
	void testCreateConvertPDFtoXDPMock() throws Exception {
		String expectedResultString = "Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		final MockPdfUtilityService underTest = MockPdfUtilityService.createConvertPDFtoXDPMock(expectedResultDoc);
		
		Document inputDoc = mockDocumentFactory.create("Input Doc".getBytes());
		final Document result = underTest.convertPDFtoXDP(inputDoc);
		
		assertSame(inputDoc, underTest.getInputDoc());
		assertSame(expectedResultDoc, result);
	}

	@Test
	void testCreateExceptionalMock_Clone() throws Exception {
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		String message = "ExceptionMessage";
		final MockPdfUtilityService underTest = MockPdfUtilityService.createExceptionalMock(message);
		
		Document inputDoc = mockDocumentFactory.create("Input Doc".getBytes());
		final PdfUtilityException ex1 = assertThrows(PdfUtilityException.class, ()->underTest.clone(inputDoc));
		String msg = ex1.getMessage();
		assertNotNull(msg);
		assertSame(message, msg);
	}

	@Test
	void testCreateExceptionalMock_ConvertPDFtoXDP() throws Exception {
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		String message = "ExceptionMessage";
		final MockPdfUtilityService underTest = MockPdfUtilityService.createExceptionalMock(message);
		
		Document inputDoc = mockDocumentFactory.create("Input Doc".getBytes());
		final PdfUtilityException ex1 = assertThrows(PdfUtilityException.class, ()->underTest.convertPDFtoXDP(inputDoc));
		String msg = ex1.getMessage();
		assertNotNull(msg);
		assertSame(message, msg);
	}

}
