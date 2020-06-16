package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;

public class AdobeAssemblerResultImpl implements AssemblerResult {

	
	com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
	
    private Map<String, Document> getDocuments;  
    
	@Override
	public Map<String, Document> getDocuments() {
		return getDocuments;
	}
	
	public AdobeAssemblerResultImpl(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		this.assemblerResult = assemblerResult;
	}

	

}
