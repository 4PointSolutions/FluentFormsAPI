package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;

public class PDFAConversionResultImpl implements PDFAConversionResult {
   
	private final Document conversionLog;
	private final Document jgetJobLog;
	private final Document pdfADocument;
	private final Boolean isPDFA;
	
	public PDFAConversionResultImpl(Document conversionLog, Document jobLog, Document pdfADocument, Boolean isPDFA) {
		this.conversionLog = conversionLog;
		this.jgetJobLog = jobLog;
		this.pdfADocument = pdfADocument;
		this.isPDFA = isPDFA;
	}

	@Override
	public Document getConversionLog() {
		return this.conversionLog;
	}

	@Override
	public Document getJobLog() {	
		return this.jgetJobLog;
	}

	@Override
	public Document getPDFADocument() {
		return this.pdfADocument;
	}

	@Override
	public Boolean isPDFA() {
		return this.isPDFA;
	}

}
