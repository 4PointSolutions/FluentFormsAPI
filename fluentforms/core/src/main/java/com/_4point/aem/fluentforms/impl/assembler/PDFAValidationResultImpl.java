package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;

public class PDFAValidationResultImpl implements PDFAValidationResult {
   
	private final Document getJobLog;
	private final Document getValidataionLog;
	private final Boolean isPDFA;
	
	PDFAValidationResultImpl(Document getJobLog, Document getValidataionLog, Boolean isPDFA) {
		this.getJobLog = getJobLog;
		this.getValidataionLog = getValidataionLog;
		this.isPDFA = isPDFA;
	}

	@Override
	public Document getJobLog() {	
		return this.getJobLog;
	}

	@Override
	public Document getValidationLog() {
		return this.getValidataionLog;
	}

	@Override
	public boolean isPDFA() {
		return this.isPDFA;
	}

}
