package com._4point.aem.fluentforms.testing.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs2;

class MockFormsServiceTest {

	@Test
	void testCreateRenderFormMockDocument() throws Exception {
		String expectedResultString = "Mock Forms Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		MockFormsService underTest = MockFormsService.createRenderFormMock(expectedResultDoc);
		
		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
		PathOrUrl template = PathOrUrl.fromString("crx:/test/form");
		Document result = underTest.renderPDFForm()
								   .executeOn(template, data);
		
		assertEquals(result, expectedResultDoc);
		RenderPDFFormArgs renderPDFFormArgs = underTest.getRenderPDFFormArgs();
		assertEquals(template.getCrxUrl(), renderPDFFormArgs.getUrlOrfilename());
		assertEquals(data, renderPDFFormArgs.getData());
	}

	@Test
	void testCreateRenderFormMockDocumentFactoryDocument() throws Exception {
		String expectedResultString = "Mock Forms Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		MockFormsService underTest = MockFormsService.createRenderFormMock(mockDocumentFactory, expectedResultDoc);
		
		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
		PathOrUrl template = PathOrUrl.fromString("crx:/test/form");
		Document result = underTest.renderPDFForm()
								   .executeOn(template, data);
		
		assertEquals(result, expectedResultDoc);
		RenderPDFFormArgs renderPDFFormArgs = underTest.getRenderPDFFormArgs();
		assertEquals(template.getCrxUrl(), renderPDFFormArgs.getUrlOrfilename());
		assertEquals(data, renderPDFFormArgs.getData());
	}

	@Test
	void testCreateRenderForm2MockDocument() throws Exception {
		String expectedResultString = "Mock Forms Test Result";
		String templateDataString = "Mock Template Bytes";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document expectedResultDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		MockFormsService underTest = MockFormsService.createRenderFormMock(expectedResultDoc);
		
		Document data = mockDocumentFactory.create(expectedResultString.getBytes());
		Document template = mockDocumentFactory.create(templateDataString.getBytes());
		Document result = underTest.renderPDFForm()
								   .executeOn(template, data);
		
		assertEquals(result, expectedResultDoc);
		RenderPDFFormArgs2 renderPDFFormArgs = underTest.getRenderPDFFormArgs2();
		assertEquals(template, renderPDFFormArgs.getTemplate());
		assertEquals(data, renderPDFFormArgs.getData());
	}

}
