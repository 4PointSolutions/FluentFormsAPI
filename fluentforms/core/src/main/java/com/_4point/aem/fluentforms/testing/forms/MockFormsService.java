package com._4point.aem.fluentforms.testing.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.forms.SafeFormsServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ExportDataArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ImportDataArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ValidateArgs;

/**
 * MockFormsService can be used to mock calls to the FormsService.
 * 
 * Calls to this object's methods can return specific results and the argument passed in those calls
 * are captured for later retrieval.  You can also create a service that always returns a FormsServiceException
 * in order to test exception handling code.
 *
 * Methods in this object with no JavaDocs rely on methods in other objects that have not yet been implemented.
 */
public class MockFormsService extends FormsServiceImpl {

	/**
	 * Creates a MockFormsService
	 */
	public MockFormsService() {
		super(new MockTraditionalFormsService(), UsageContext.SERVER_SIDE);
	}

	/**
	 * Creates a MockFormsService
	 * 
	 * @param documentFactory
	 */
	public MockFormsService(DocumentFactory documentFactory) {
		super(new MockTraditionalFormsService(documentFactory), UsageContext.SERVER_SIDE);
	}

	/**
	 * Creates a MockFormsService
	 * 
	 * @param formsService
	 */
	public MockFormsService(TraditionalFormsService formsService) {
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

	/**
	 * Creates a MockFormsService that returns a specific result when renderPDFForm is called.
	 * 
	 * @param renderPDFFormResult
	 * @return
	 */
	public static MockFormsService createRenderFormMock(Document renderPDFFormResult) {
		return new MockFormsService(new MockTraditionalFormsService().setRenderPDFFormResult(renderPDFFormResult));
	}

	/**
	 * Creates a MockFormsService that returns a specific result when renderPDFForm is called.
	 * 
	 * @param documentFactory
	 * @param renderPDFFormResult
	 * @return
	 */
	public static MockFormsService createRenderFormMock(DocumentFactory documentFactory, Document renderPDFFormResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setRenderPDFFormResult(renderPDFFormResult));
	}

	public static MockFormsService createValidateMock(ValidationResult validateResult) {
		return new MockFormsService(new MockTraditionalFormsService().setValidateResult(validateResult));
	}

	public static MockFormsService createValidateMock(DocumentFactory documentFactory, ValidationResult validateResult) {
		return new MockFormsService(new MockTraditionalFormsService(documentFactory).setValidateResult(validateResult));
	}

	/**
	 * Creates a MockFormsService that throws a FormServiceException with a specific message.
	 * 
	 * @param message Message to be included in the exception.
	 * @return
	 */
	public static MockFormsService createExceptionalMock(String message) {
		return new MockFormsService(ExceptionalMockTraditionalFormsService.create(message));
	}
	
	public MockFormsService setExportDataResult(Document exportDataResult) {
		getMockService().setExportDataResult(exportDataResult);
		return this;
	}

	public MockFormsService setImportDataResult(Document importDataResult) {
		getMockService().setImportDataResult(importDataResult);
		return this;
	}

	/**
	 * Set the result of the renderPDFForm operation.
	 * 
	 * @param renderPDFFormResult
	 * @return
	 */
	public MockFormsService setRenderPDFFormResult(Document renderPDFFormResult) {
		getMockService().setRenderPDFFormResult(renderPDFFormResult);
		return this;
	}

	public MockFormsService setValidateResult(ValidationResult validateResult) {
		getMockService().setValidateResult(validateResult);
		return this;
	}

	public ExportDataArgs getExportDataArgs() {
		return getMockService().getExportDataArgs();
	}

	public ImportDataArgs getImportDataArgs() {
		return getMockService().getImportDataArgs();
	}

	/**
	 * Get the arguments from the renderPDFForm operation.
	 * 
	 * @return
	 */
	public RenderPDFFormArgs getRenderPDFFormArgs() {
		return getMockService().getRenderPDFFormArgs();
	}

	public ValidateArgs getValidateArgs() {
		return getMockService().getValidateArgs();
	}

	private MockTraditionalFormsService getMockService() {
		return (MockTraditionalFormsService)(((SafeFormsServiceAdapterWrapper)this.getAdobeFormsService()).getFormsService());
	}

}
