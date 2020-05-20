package com._4point.aem.fluentforms.impl.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com.adobe.fd.forms.api.DataFormat;

public interface TraditionalFormsService {

	Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException;

	Document importData(Document pdf, Document data) throws FormsServiceException;

	Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException;

	Document renderPDFForm(Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException;
	
	ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException;

}