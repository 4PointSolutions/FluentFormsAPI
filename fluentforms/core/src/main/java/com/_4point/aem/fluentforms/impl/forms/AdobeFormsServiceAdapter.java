package com._4point.aem.fluentforms.impl.forms;

import org.osgi.service.component.annotations.Reference;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class AdobeFormsServiceAdapter implements TraditionalFormsService {
	
	private final DocumentFactory documentFactory;

	@Reference
	private com.adobe.fd.forms.api.FormsService adobeFormsService;

	public AdobeFormsServiceAdapter() {
		super();
		this.documentFactory = DocumentFactory.getDefault();
	}

	public AdobeFormsServiceAdapter(DocumentFactory documentFactory) {
		super();
		this.documentFactory = documentFactory;
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.exportData(pdfOrXdp.getAdobeDocument(), dataFormat));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.importData(pdf.getAdobeDocument(), data.getAdobeDocument()));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.renderPDFForm(urlOrfilename, data.getAdobeDocument(), pdfFormRenderOptions));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions) throws FormsServiceException {
		try {
			return new ValidationResultImpl(adobeFormsService.validate(template, data.getAdobeDocument(), validationOptions.toAdobeValidationOptions()), documentFactory);
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

}