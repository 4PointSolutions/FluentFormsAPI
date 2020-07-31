package com._4point.aem.fluentforms.impl.generatePDF;

import java.io.IOException;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;

public class CreatePDFResultImpl implements CreatePDFResult {
	private Document getCreatedDocument;
	private Document getLogDocument;

	@Override
	public Document getCreatedDocument() {
		return getCreatedDocument;
	}

	@Override
	public Document getLogDocument() {
		return getLogDocument;
	}

	public void setGetCreatedDocument(Document getCreatedDocument) {
		this.getCreatedDocument = getCreatedDocument;
	}

	public void setGetLogDocument(Document getLogDocument) {
		this.getLogDocument = getLogDocument;
	}

	@Override
	public void close() throws IOException {

	}

}
