package com._4point.aem.fluentforms.api.generatePDF;

import java.io.Closeable;

import com._4point.aem.fluentforms.api.Document;

public interface CreatePDFResult extends Closeable {

	Document getCreatedDocument();
	Document getLogDocument();

}
