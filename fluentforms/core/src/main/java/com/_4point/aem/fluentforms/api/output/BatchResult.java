package com._4point.aem.fluentforms.api.output;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;

public interface BatchResult {

	List<Document> getGeneratedDocs();

	Document getMetaDataDoc();

}