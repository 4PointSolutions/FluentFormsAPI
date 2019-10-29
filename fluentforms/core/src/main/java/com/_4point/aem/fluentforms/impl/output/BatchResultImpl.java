package com._4point.aem.fluentforms.impl.output;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchResult;

public class BatchResultImpl implements BatchResult {

	public static BatchResultImpl create(List<Document> getGeneratedDocs, Document getMetaDataDoc) {
		return new BatchResultImpl(getGeneratedDocs, getMetaDataDoc);
	}

	private BatchResultImpl(List<Document> getGeneratedDocs, Document getMetaDataDoc) {
		super();
		this.getGeneratedDocs = getGeneratedDocs;
		this.getMetaDataDoc = getMetaDataDoc;
	}

	private final List<Document> getGeneratedDocs;
	private final Document getMetaDataDoc;

	@Override
	public List<Document> getGeneratedDocs() {
		return this.getGeneratedDocs;
	}

	@Override
	public Document getMetaDataDoc() {
		return this.getMetaDataDoc;
	}
}
