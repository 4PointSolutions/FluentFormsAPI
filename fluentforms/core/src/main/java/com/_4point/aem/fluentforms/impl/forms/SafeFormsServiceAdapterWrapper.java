package com._4point.aem.fluentforms.impl.forms;

import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class SafeFormsServiceAdapterWrapper implements TraditionalFormsService {
	
	private final TraditionalFormsService formsService;
	
	public SafeFormsServiceAdapterWrapper(TraditionalFormsService formsService) {
		super();
		this.formsService = formsService;
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		Objects.requireNonNull(pdfOrXdp, "pdfOrXdp cannot be null");
		Objects.requireNonNull(dataFormat, "dataFormat cannot be null");
		return formsService.exportData(pdfOrXdp, dataFormat);
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		Objects.requireNonNull(pdf, "pdf cannot be null");
		Objects.requireNonNull(data, "data connot be null.");
		return formsService.importData(pdf, data);
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(urlOrfilename, "urlOrFilename cannot be null.");  // Already handled above.
//		Objects.requireNonNull(data, "data cannot be null.");	// Data can be null to produce empty form.
		Objects.requireNonNull(pdfFormRenderOptions, "pdfFormRenderOptions cannot be null.");
		return formsService.renderPDFForm(urlOrfilename, data, pdfFormRenderOptions);
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		Objects.requireNonNull(template, "template cannot be null.");
		Objects.requireNonNull(data, "data cannot be null.");
		Objects.requireNonNull(validationOptions, "validationOptions cannot be null.");
		return formsService.validate(template, data, validationOptions);
	}

}
