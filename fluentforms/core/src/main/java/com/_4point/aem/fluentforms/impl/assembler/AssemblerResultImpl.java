package com._4point.aem.fluentforms.impl.assembler;

import java.util.List;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com.adobe.fd.assembler.client.OperationException;

public class AssemblerResultImpl implements AssemblerResult {
		
	private final Map<String,Document> documents;
	private final List<String> failedBlockNames;
	private final Document jobLog;
	private final int lastBatesNumber;
	private final Map<String,List<String>> multipleResultsBlocks;
	private final int numRequestedBlocks;
	private final List<String> successfulBlockNames;
	private final List<String> successfulDocumentNames;
	private final Map<String,OperationException> throwables;
	
	public AssemblerResultImpl(Map<String, Document> documents, List<String> failedBlockNames, Document jobLog,
			int lastBatesNumber, Map<String, List<String>> multipleResultsBlocks, int numRequestedBlocks,
			List<String> successfulBlockNames, List<String> successfulDocumentNames,
			Map<String, OperationException> throwables) {
		this.documents = documents;
		this.failedBlockNames = failedBlockNames;
		this.jobLog = jobLog;
		this.lastBatesNumber = lastBatesNumber;
		this.multipleResultsBlocks = multipleResultsBlocks;
		this.numRequestedBlocks = numRequestedBlocks;
		this.successfulBlockNames = successfulBlockNames;
		this.successfulDocumentNames = successfulDocumentNames;
		this.throwables = throwables;
	}

	@Override
	public Map<String, Document> getDocuments() {	
		return documents;
	}

	@Override
	public List<String> getFailedBlockNames() {	
		return failedBlockNames;
	}

	@Override
	public Document getJobLog() {	
		return this.jobLog;
	}

	@Override
	public int getLastBatesNumber() {	
		return lastBatesNumber;
	}

	@Override
	public Map<String, List<String>> getMultipleResultsBlocks() {	
		return multipleResultsBlocks;
	}

	@Override
	public int getNumRequestedBlocks() {	
		return numRequestedBlocks;
	}

	@Override
	public List<String> getSuccessfulBlockNames() {	
		return successfulBlockNames;
	}

	@Override
	public List<String> getSuccessfulDocumentNames() {
		return successfulDocumentNames;
	}

	@Override
	public Map<String, OperationException> getThrowables() {
		return throwables;
	}
}
