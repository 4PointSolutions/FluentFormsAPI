package com._4point.aem.fluentforms.impl.forms;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.ValidationResult;

public class ValidationResultImpl implements ValidationResult {

	private final Document document;
	private final Document validationResultDoc;
	
	protected ValidationResultImpl(com.adobe.fd.forms.api.ValidationResult validationResult) {
		super();
		this.document = DocumentFactory.getDefault().create(validationResult.getDocument());
		this.validationResultDoc = DocumentFactory.getDefault().create(validationResult.getValidationResult());
	}

	protected ValidationResultImpl(com.adobe.fd.forms.api.ValidationResult validationResult, DocumentFactory documentFactory) {
		super();
		this.document = documentFactory.create(validationResult.getDocument());
		this.validationResultDoc = documentFactory.create(validationResult.getValidationResult());
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationResult#getDocument()
	 */
	@Override
	public Document getDocument() {
		return this.document;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationResult#getValidationResult()
	 */
	@Override
	public Document getValidationResult() {
		return this.validationResultDoc;
	}

}
