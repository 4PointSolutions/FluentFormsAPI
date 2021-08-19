package com._4point.aem.fluentforms.testing.pdfUtility;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public class ExceptionalMockTraditionalPdfUtilityService implements TraditionalPdfUtilityService {
	private final String message;

	private ExceptionalMockTraditionalPdfUtilityService(String message) {
		this.message = message;
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	@Override
	public PDFPropertiesResult getPDFProperties(Document doc, PDFPropertiesOptionSpec pdfPropOptionsSpec)
			throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	@Override
	public List<Document> multiclone(Document doc, int numClones) throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	@Override
	public RedactionResult redact(Document doc, RedactionOptionSpec redactOptionsSpec) throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	@Override
	public SanitizationResult sanitize(Document doc) throws PdfUtilityException {
		throw new PdfUtilityException(this.message);
	}

	public static ExceptionalMockTraditionalPdfUtilityService create(String message) {
		return new ExceptionalMockTraditionalPdfUtilityService(message);
	}
}