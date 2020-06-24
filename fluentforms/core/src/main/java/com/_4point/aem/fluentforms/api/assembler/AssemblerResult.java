package com._4point.aem.fluentforms.api.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;

public interface AssemblerResult extends AutoCloseable{
	
	 Map<String,Document> getDocuments();
	

}
