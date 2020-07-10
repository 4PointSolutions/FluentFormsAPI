package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;

public class PDFAValidationResultImpl implements PDFAValidationResult {
   
	private Document getJobLog;
	private Document getValidataionLog;
	private Boolean isPDFA;
	
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
