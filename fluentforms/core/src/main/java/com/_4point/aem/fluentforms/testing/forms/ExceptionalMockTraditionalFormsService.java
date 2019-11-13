package com._4point.aem.fluentforms.testing.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;

public class ExceptionalMockTraditionalFormsService implements TraditionalFormsService {
	private final String message;
	
	private ExceptionalMockTraditionalFormsService(String message) {
		super();
		this.message = message;
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		throw new FormsServiceException(this.message);
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		throw new FormsServiceException(this.message);
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		throw new FormsServiceException(this.message);
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		throw new FormsServiceException(this.message);
	}

	public static ExceptionalMockTraditionalFormsService create(String message) {
		return new ExceptionalMockTraditionalFormsService(message);
	}
}
