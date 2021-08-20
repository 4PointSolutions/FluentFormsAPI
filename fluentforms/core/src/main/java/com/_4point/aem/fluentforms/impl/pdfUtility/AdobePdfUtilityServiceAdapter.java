package com._4point.aem.fluentforms.impl.pdfUtility;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.PDFUtilityException;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public class AdobePdfUtilityServiceAdapter implements TraditionalPdfUtilityService {
	private final DocumentFactory documentFactory;
	private final com.adobe.fd.pdfutility.services.PDFUtilityService adobePdfUtilityService;

	public AdobePdfUtilityServiceAdapter(com.adobe.fd.pdfutility.services.PDFUtilityService adobePdfUtilityService) {
		this.documentFactory = DocumentFactory.getDefault();
		this.adobePdfUtilityService = requireNonNull(adobePdfUtilityService, "Adobe Pdf Utility Service cannot be null.");
	}

	public AdobePdfUtilityServiceAdapter(com.adobe.fd.pdfutility.services.PDFUtilityService adobePdfUtilityService, DocumentFactory documentFactory) {
		this.documentFactory = requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.adobePdfUtilityService = requireNonNull(adobePdfUtilityService, "Adobe Pdf Utility Service cannot be null.");
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		try {
			return documentFactory.create(adobePdfUtilityService.clone(AdobeDocumentFactoryImpl.getAdobeDocument(doc)));
		} catch (PDFUtilityException e) {
			throw new PdfUtilityException(e);
		}
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		try {
			return documentFactory.create(adobePdfUtilityService.convertPDFtoXDP(AdobeDocumentFactoryImpl.getAdobeDocument(doc)));
		} catch (PDFUtilityException e) {
			throw new PdfUtilityException(e);
		}
	}

	@Override
	public PDFPropertiesResult getPDFProperties(Document cod, PDFPropertiesOptionSpec pdfPropOptionsSpec)
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

}
