package com._4point.aem.fluentforms.testing.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ExportDataArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ImportDataArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ValidateArgs;

public class MockFormsService extends FormsServiceImpl {

	public MockFormsService() {
		super(new MockTraditionalFormsService(), UsageContext.SERVER_SIDE);
	}

	public MockFormsService(DocumentFactory documentFactory) {
		super(new MockTraditionalFormsService(documentFactory), UsageContext.SERVER_SIDE);
	}

	public MockFormsService(MockTraditionalFormsService formsService) {
		super(formsService, UsageContext.SERVER_SIDE);
	}

	public static MockFormsService createExportDataMock(Document exportDataResult) {
		return new MockFormsService(new MockTraditionalFormsService().setExportDataResult(exportDataResult)); 
	}

	public static MockFormsService createExportDataMock(DocumentFactory documentFactory, Document exportDataResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setExportDataResult(exportDataResult)); 
	}

	public static MockFormsService createImportDataMock(Document importDataResult) {
		return new MockFormsService(new MockTraditionalFormsService().setImportDataResult(importDataResult));
	}

	public static MockFormsService createImportDataMock(DocumentFactory documentFactory, Document importDataResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setImportDataResult(importDataResult));
	}

	public static MockFormsService createRenderFormMock(Document renderPDFFormResult) {
		return new MockFormsService(new MockTraditionalFormsService().setRenderPDFFormResult(renderPDFFormResult));
	}

	public static MockFormsService createRenderFormMock(DocumentFactory documentFactory, Document renderPDFFormResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setRenderPDFFormResult(renderPDFFormResult));
	}

	public static MockFormsService createValidateMock(ValidationResult validateResult) {
		return new MockFormsService(new MockTraditionalFormsService().setValidateResult(validateResult));
	}

	public static MockFormsService createValidateMock(DocumentFactory documentFactory, ValidationResult validateResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setValidateResult(validateResult));
	}
	
	public MockFormsService setExportDataResult(Document exportDataResult) {
		((MockTraditionalFormsService)(this.getAdobeFormsService())).setExportDataResult(exportDataResult);
		return this;
	}

	public MockFormsService setImportDataResult(Document importDataResult) {
		((MockTraditionalFormsService)(this.getAdobeFormsService())).setImportDataResult(importDataResult);
		return this;
	}

	public MockFormsService setRenderPDFFormResult(Document renderPDFFormResult) {
		((MockTraditionalFormsService)(this.getAdobeFormsService())).setRenderPDFFormResult(renderPDFFormResult);
		return this;
	}

	public MockFormsService setValidateResult(ValidationResult validateResult) {
		((MockTraditionalFormsService)(this.getAdobeFormsService())).setValidateResult(validateResult);
		return this;
	}

	public ExportDataArgs getExportDataArgs() {
		return ((MockTraditionalFormsService)(this.getAdobeFormsService())).getExportDataArgs();
	}

	public ImportDataArgs getImportDataArgs() {
		return ((MockTraditionalFormsService)(this.getAdobeFormsService())).getImportDataArgs();
	}

	public RenderPDFFormArgs getRenderPDFFormArgs() {
		return ((MockTraditionalFormsService)(this.getAdobeFormsService())).getRenderPDFFormArgs();
	}

	public ValidateArgs getValidateArgs() {
		return ((MockTraditionalFormsService)(this.getAdobeFormsService())).getValidateArgs();
	}

}
