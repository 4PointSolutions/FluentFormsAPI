package com._4point.aem.fluentforms.api.assembler;

import java.util.List;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com.adobe.fd.assembler.client.OperationException;

public interface AssemblerResult {
	
	 Map<String,Document> getDocuments();
	
	 List<String> getFailedBlockNames();
    
     Document etJobLog();
     
     int getLastBatesNumber();
     
     Map<String,List<String>> getMultipleResultsBlocks();
     
     int getNumRequestedBlocks();
     
     List<String> getSuccessfulBlockNames();
    
     List<String> getSuccessfulDocumentNames();
    
     Map<String,OperationException>	getThrowables();
     
}
