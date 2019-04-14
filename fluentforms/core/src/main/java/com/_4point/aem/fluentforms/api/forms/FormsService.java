package com._4point.aem.fluentforms.api.forms;

import java.net.URL;
import java.nio.file.Path;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public interface FormsService {

	Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException;

	Document importData(Document pdf, Document data) throws FormsServiceException;

	Document renderPDFForm(Path filename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException;

	Document renderPDFForm(URL url, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException;

	RenderPDFFormArgumentBuilder renderPDFForm();

	ValidationResult validate(Path template, Document data, ValidationOptions validationOptions) throws FormsServiceException;

	ValidateArgumentBuilder validate();

	public static class FormsServiceException extends Exception {

		private static final long serialVersionUID = -9187778886719471016L;

		public FormsServiceException() {
			super();
		}

		public FormsServiceException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public FormsServiceException(String arg0) {
			super(arg0);
		}

		public FormsServiceException(Throwable arg0) {
			super(arg0);
		}
	}
	
	public static interface RenderPDFFormArgumentBuilder extends PDFFormRenderOptionsSetter, Transformable<RenderPDFFormArgumentBuilder> {
		public Document executeOn(Path template, Document data) throws FormsServiceException;
	}
	
	public static interface ValidateArgumentBuilder extends ValidationOptionsSetter, Transformable<ValidateArgumentBuilder> {
		public ValidationResult executeOn(Path template, Document data) throws FormsServiceException;
	}

}
