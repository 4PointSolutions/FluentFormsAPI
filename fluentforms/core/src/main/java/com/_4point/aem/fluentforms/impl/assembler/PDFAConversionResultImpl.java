package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;

public class PDFAConversionResultImpl implements PDFAConversionResult {
   
	private Document getConversionLog;
	private Document getJobLog;
	private Document getPDFADocument;
	private Boolean isPDFA;
	
	@Override
	public Document getConversionLog() {
		return this.getConversionLog;
	}

	@Override
	public Document getJobLog() {	
		return this.getJobLog;
	}

	@Override
	public Document getPDFADocument() {
		return this.getPDFADocument;
	}

	@Override
	public Boolean isPDFA() {
		return this.isPDFA;
	}

}
