package com._4point.aem.fluentforms.testing.pdfUtility;

import static java.util.Objects.isNull;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public class MockTraditionalPdfUtilityService implements TraditionalPdfUtilityService {
	private final DocumentFactory documentFactory;
	private final Document DUMMY_DOCUMENT;

	Document inputDoc;
	Document result;

	public MockTraditionalPdfUtilityService() {
		this.documentFactory = new MockDocumentFactory();
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}

	public MockTraditionalPdfUtilityService(DocumentFactory documentFactory) {
		this.documentFactory = documentFactory;
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}

	public static MockTraditionalPdfUtilityService createDocumentMock(Document doc) {
		return new MockTraditionalPdfUtilityService().setResult(doc);
	}

	public static MockTraditionalPdfUtilityService createDocumentMock(DocumentFactory docFactory, Document doc) {
		return new MockTraditionalPdfUtilityService(docFactory).setResult(doc);
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		this.inputDoc = doc;
		return isNull(this.result) ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		this.inputDoc = doc;
		return isNull(this.result) ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public PDFPropertiesResult getPDFProperties(Document doc, PDFPropertiesOptionSpec pdfPropOptionsSpec)
			throws PdfUtilityException {
		throw new UnsupportedOperationException("getPdfProperties method has not been implemented yet.");
	}

	@Override
	public List<Document> multiclone(Document doc, int numClones) throws PdfUtilityException {
		throw new UnsupportedOperationException("multiclone method has not been implemented yet.");
	}

	@Override
	public RedactionResult redact(Document doc, RedactionOptionSpec redactOptionsSpec) throws PdfUtilityException {
		throw new UnsupportedOperationException("redact method has not been implemented yet.");
	}

	@Override
	public SanitizationResult sanitize(Document doc) throws PdfUtilityException {
		throw new UnsupportedOperationException("sanitize method has not been implemented yet.");
	}

	public MockTraditionalPdfUtilityService setResult(Document document) {
		this.result = document;
		return this;
	}
	
	public Document getInputDoc() {
		return this.inputDoc;
	}
}
