package com._4point.aem.fluentforms.api.assembler;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com.adobe.fd.assembler.client.OperationException;

public interface AssemblerResult extends Closeable {
	
	 Map<String,Document> getDocuments();
	
	 List<String> getFailedBlockNames();
    
	 Document getJobLog();
     
     int getLastBatesNumber();
     
     Map<String,List<String>> getMultipleResultsBlocks();
     
     int getNumRequestedBlocks();
     
     List<String> getSuccessfulBlockNames();
    
     List<String> getSuccessfulDocumentNames();
    
     Map<String,OperationException>	getThrowables();
     
}
