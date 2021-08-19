package com._4point.aem.fluentforms.impl.pdfUtility;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public interface TraditionalPdfUtilityService {

	public Document clone(Document doc) throws PdfUtilityException;

	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException;

	public PDFPropertiesResult getPDFProperties(Document doc, PDFPropertiesOptionSpec pdfPropOptionsSpec) throws PdfUtilityException;

	public List<Document> multiclone(Document doc, int numClones) throws PdfUtilityException;

	public RedactionResult redact(Document doc, RedactionOptionSpec redactOptionsSpec) throws PdfUtilityException;

	public SanitizationResult sanitize(Document doc) throws PdfUtilityException;
}
