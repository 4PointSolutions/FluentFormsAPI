package com._4point.aem.fluentforms.testing.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class MockTraditionalFormsService implements TraditionalFormsService {
	private final DocumentFactory documentFactory;
	private final Document DUMMY_DOCUMENT;
	private Document exportDataResult = null;
	private Document importDataResult = null;
	private Document renderPDFFormResult = null;
	private ValidationResult validateResult = null;
	private ExportDataArgs exportDataArgs = null;
	private ImportDataArgs importDataArgs = null;
	private RenderPDFFormArgs renderPDFFormArgs = null;
	private ValidateArgs validateArgs = null;
	
	public MockTraditionalFormsService() {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}
	
	public MockTraditionalFormsService(DocumentFactory documentFactory) {
		super();
		this.documentFactory = documentFactory;
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}

	public static MockTraditionalFormsService createExportDataMock(Document exportDataResult) {
		return new MockTraditionalFormsService().setExportDataResult(exportDataResult); 
	}

	public static MockTraditionalFormsService createExportDataMock(DocumentFactory documentFactory, Document exportDataResult) {
		return new MockTraditionalFormsService(documentFactory).setExportDataResult(exportDataResult); 
	}

	public static MockTraditionalFormsService createImportDataMock(Document importDataResult) {
		return new MockTraditionalFormsService().setImportDataResult(importDataResult);
	}

	public static MockTraditionalFormsService createImportDataMock(DocumentFactory documentFactory, Document importDataResult) {
		return new MockTraditionalFormsService(documentFactory).setImportDataResult(importDataResult);
	}

	public static MockTraditionalFormsService createRenderFormMock(Document renderPDFFormResult) {
		return new MockTraditionalFormsService().setRenderPDFFormResult(renderPDFFormResult);
	}

	public static MockTraditionalFormsService createRenderFormMock(DocumentFactory documentFactory, Document renderPDFFormResult) {
		return new MockTraditionalFormsService(documentFactory).setRenderPDFFormResult(renderPDFFormResult);
	}

	public static MockTraditionalFormsService createValidateMock(ValidationResult validateResult) {
		return new MockTraditionalFormsService().setValidateResult(validateResult);
	}

	public static MockTraditionalFormsService createValidateMock(DocumentFactory documentFactory, ValidationResult validateResult) {
		return new MockTraditionalFormsService(documentFactory).setValidateResult(validateResult);
	}
	
	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		this.exportDataArgs = new ExportDataArgs(pdfOrXdp, dataFormat); 
		return exportDataResult == null ? DUMMY_DOCUMENT : exportDataResult;
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		this.importDataArgs = new ImportDataArgs(pdf, data);
		return importDataResult == null ? DUMMY_DOCUMENT : importDataResult;
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		this.renderPDFFormArgs = new RenderPDFFormArgs(urlOrfilename, data, pdfFormRenderOptions);
		return renderPDFFormResult == null ? DUMMY_DOCUMENT : renderPDFFormResult;
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		this.validateArgs = new ValidateArgs(template, data, validationOptions);
		return validateResult  != null ? validateResult : new ValidationResult() {

			@Override
			public Document getDocument() {
				return DUMMY_DOCUMENT;
			}

			@Override
			public Document getValidationResult() {
				return DUMMY_DOCUMENT;
			}};
	}

	
	public MockTraditionalFormsService setExportDataResult(Document exportDataResult) {
		this.exportDataResult = exportDataResult;
		return this;
	}

	public MockTraditionalFormsService setImportDataResult(Document importDataResult) {
		this.importDataResult = importDataResult;
		return this;
	}

	public MockTraditionalFormsService setRenderPDFFormResult(Document renderPDFFormResult) {
		this.renderPDFFormResult = renderPDFFormResult;
		return this;
	}

	public MockTraditionalFormsService setValidateResult(ValidationResult validateResult) {
		this.validateResult = validateResult;
		return this;
	}

	public ExportDataArgs getExportDataArgs() {
		return exportDataArgs;
	}

	public ImportDataArgs getImportDataArgs() {
		return importDataArgs;
	}

	public RenderPDFFormArgs getRenderPDFFormArgs() {
		return renderPDFFormArgs;
	}

	public ValidateArgs getValidateArgs() {
		return validateArgs;
	}

	public static class ExportDataArgs {
		private final Document pdfOrXdp;
		private final DataFormat dataFormat;
		public ExportDataArgs(Document pdfOrXdp, DataFormat dataFormat) {
			super();
			this.pdfOrXdp = pdfOrXdp;
			this.dataFormat = dataFormat;
		}
		public Document getPdfOrXdp() {
			return pdfOrXdp;
		}
		public DataFormat getDataFormat() {
			return dataFormat;
		}
	}
	
	public static class ImportDataArgs {
		private final Document pdf;
		private final Document data;
		public ImportDataArgs(Document pdf, Document data) {
			super();
			this.pdf = pdf;
			this.data = data;
		}
		public Document getPdf() {
			return pdf;
		}
		public Document getData() {
			return data;
		}
	}
	
	public static class RenderPDFFormArgs {
		private final String urlOrfilename;
		private final Document data;
		private final PDFFormRenderOptions pdfFormRenderOptions;
		public RenderPDFFormArgs(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) {
			super();
			this.urlOrfilename = urlOrfilename;
			this.data = data;
			this.pdfFormRenderOptions = pdfFormRenderOptions;
		}
		public String getUrlOrfilename() {
			return urlOrfilename;
		}
		public Document getData() {
			return data;
		}
		public PDFFormRenderOptions getPdfFormRenderOptions() {
			return pdfFormRenderOptions;
		}
		
	}
	
	public static class ValidateArgs {
		private final String template;
		private final Document data;
		private final ValidationOptions validationOptions;
		public ValidateArgs(String template, Document data, ValidationOptions validationOptions) {
			super();
			this.template = template;
			this.data = data;
			this.validationOptions = validationOptions;
		}
		public String getTemplate() {
			return template;
		}
		public Document getData() {
			return data;
		}
		public ValidationOptions getValidationOptions() {
			return validationOptions;
		}
	}
}
