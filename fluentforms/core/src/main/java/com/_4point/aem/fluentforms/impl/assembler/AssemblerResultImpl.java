package com._4point.aem.fluentforms.impl.assembler;

import java.util.List;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com.adobe.fd.assembler.client.OperationException;

public class AssemblerResultImpl implements AssemblerResult {
    
	private Map<String, Document> getDocuments;  
	
	private  List<String> getFailedBlockNames;
	    
	private Document etJobLog;;
     
	private int getLastBatesNumber;
     
	private  Map<String,List<String>> getMultipleResultsBlocks;
     
	private int getNumRequestedBlocks;
     
	private List<String> getSuccessfulBlockNames;
    
	private List<String> getSuccessfulDocumentNames;
    
	private Map<String,OperationException>	getThrowables;
	
	public AssemblerResultImpl(Map<String, Document> getDocuments) {
		super();
		this.getDocuments = getDocuments;
	}
	
	
	@Override
	public Map<String, Document> getDocuments() {
			return getDocuments;
	}


	@Override
	public void close() throws Exception {
		
	}


	@Override
	public List<String> getFailedBlockNames() {
		return getFailedBlockNames;
	}


	@Override
	public Document etJobLog() {
		return etJobLog;
	}


	@Override
	public int getLastBatesNumber() {
		return getLastBatesNumber;
	}


	@Override
	public Map<String, List<String>> getMultipleResultsBlocks() {
		return getMultipleResultsBlocks;
	}


	@Override
	public int getNumRequestedBlocks() {
		return getNumRequestedBlocks;
	}


	@Override
	public List<String> getSuccessfulBlockNames() {
		return getSuccessfulBlockNames;
	}


	@Override
	public List<String> getSuccessfulDocumentNames() {
		return getSuccessfulDocumentNames;
	}


	@Override
	public Map<String, OperationException> getThrowables() {
		return getThrowables;
	}

}
