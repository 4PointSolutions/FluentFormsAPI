package com._4point.aem.fluentforms.testing.convertPdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService.ToImageArgs;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService.ToPSArgs;

public class MockConvertPdfServiceTest {

	@Test
	void testCreateToImageMockDocument() throws Exception {
		String expectedResultString = "Mock Convert PDF Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document inPdfDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		List<Document> expectedResultDoc = Collections.singletonList(inPdfDoc);
		MockConvertPdfService underTest = MockConvertPdfService.createToImageMock(expectedResultDoc);
		
		List<Document> result = underTest.toImage()
										.executeOn(inPdfDoc);
		
		assertEquals(result.get(0), expectedResultDoc.get(0));
		ToImageArgs toImageArgs = underTest.getToImageArgs();
		assertEquals(inPdfDoc, toImageArgs.getInPdfDoc());
	}

	@Test
	void testCreateToPSMockDocument() throws Exception {
		String expectedResultString = "Mock Convert PDF Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		MockConvertPdfService underTest = MockConvertPdfService.createToPSMock(expectedResultDoc);
		
		Document inPdfDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		Document result = underTest.toPS()
								.executeOn(inPdfDoc);
		
		assertEquals(result, expectedResultDoc);
		ToPSArgs toPSArgs = underTest.getToPSArgs();
		assertEquals(inPdfDoc, toPSArgs.getInPdfDoc());
	}
}
