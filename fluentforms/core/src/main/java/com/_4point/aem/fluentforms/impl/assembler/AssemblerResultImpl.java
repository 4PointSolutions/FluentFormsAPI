package com._4point.aem.fluentforms.impl.assembler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com.adobe.fd.assembler.client.OperationException;

public class AssemblerResultImpl implements AssemblerResult {
		
	Map<String,Document> getDocuments;

	List<String> getFailedBlockNames;

	Document getJobLog;

	int getLastBatesNumber;

	Map<String,List<String>> getMultipleResultsBlocks;

	int getNumRequestedBlocks;

	List<String> getSuccessfulBlockNames;

	List<String> getSuccessfulDocumentNames;

	Map<String,OperationException>	getThrowables;
	

	@Override
	public Map<String, Document> getDocuments() {	
		return getDocuments;
	}

	@Override
	public List<String> getFailedBlockNames() {	
		return getFailedBlockNames;
	}

	@Override
	public Document getJobLog() {	
		return this.getJobLog;
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

	 public void setDocuments(Map<String,Document> documents) {
		 this.getDocuments = documents;
	 }
  
	 public void setFailedBlockNames(List<String> failedBlockNames) {
		 this.getFailedBlockNames = failedBlockNames;
	 }
   
	 public void setJobLog(Document jobLog) {
		 this.getJobLog = jobLog;
	 }
   
	 public void setLastBatesNumber(int lastBatesNumber) {
		 this.getLastBatesNumber = lastBatesNumber;
	 }
     
	 public void setMultipleResultsBlocks(Map<String,List<String>> multipleResultsBlocks) {
		 this.getMultipleResultsBlocks = multipleResultsBlocks;
	 }
    
	 public void setNumRequestedBlocks(int numRequestedBlocks) {
		 this.getNumRequestedBlocks = numRequestedBlocks;
	 }
    
	 public void setSuccessfulBlockNames(List<String> successfulBlockNames) {
		 this.getSuccessfulBlockNames = successfulBlockNames;
	 }
   
	 public void setSuccessfulDocumentNames(List<String> successfulDocumentNames) {
		 this.getSuccessfulDocumentNames = successfulDocumentNames;
	 }
   
	 public void setThrowables(Map<String,OperationException> throwables) {
		 this.getThrowables = throwables;
	 }

	@Override
	public void close() throws IOException {
	
	}

}
