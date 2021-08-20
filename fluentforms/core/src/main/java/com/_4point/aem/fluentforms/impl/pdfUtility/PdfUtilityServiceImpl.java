package com._4point.aem.fluentforms.impl.pdfUtility;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public class PdfUtilityServiceImpl implements PdfUtilityService {
	private final TraditionalPdfUtilityService adobePdfUtilityService;

	public PdfUtilityServiceImpl(TraditionalPdfUtilityService adobePdfUtilityService) {
		super();
		this.adobePdfUtilityService = adobePdfUtilityService;
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		return adobePdfUtilityService.clone(requireNonNull(doc, "doc parameter cannot be null."));
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		return adobePdfUtilityService.convertPDFtoXDP(requireNonNull(doc, "doc parameter cannot be null."));
	}

	@Override
	public PDFPropertiesResult getPDFProperties(Document doc, PDFPropertiesOptionSpec pdfPropOptionsSpec) throws PdfUtilityException {
		return adobePdfUtilityService.getPDFProperties(requireNonNull(doc, "doc parameter cannot be null."), requireNonNull(pdfPropOptionsSpec, "pdfPropOptionsSpec parameter cannot be null."));
	}

	@Override
	public List<Document> multiclone(Document doc, int numClones) throws PdfUtilityException {
		return adobePdfUtilityService.multiclone(requireNonNull(doc, "doc parameter cannot be null."), numClones);
	}

	@Override
	public RedactionResult redact(Document doc, RedactionOptionSpec redactOptionsSpec) throws PdfUtilityException {
		return adobePdfUtilityService.redact(requireNonNull(doc, "doc parameter cannot be null."), requireNonNull(redactOptionsSpec, "redactOptionsSpec parameter cannot be null."));
	}

	@Override
	public SanitizationResult sanitize(Document doc) throws PdfUtilityException {
		return adobePdfUtilityService.sanitize(requireNonNull(doc, "doc parameter cannot be null."));
	}

	protected final TraditionalPdfUtilityService getAdobePdfUtilityService() {
		return adobePdfUtilityService;
	}
	
}
