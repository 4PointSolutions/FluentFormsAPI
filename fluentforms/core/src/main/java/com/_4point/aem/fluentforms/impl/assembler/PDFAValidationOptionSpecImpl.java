package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel;

public class PDFAValidationOptionSpecImpl implements PDFAValidationOptionSpec {

	private Boolean allowCertificationSignatures;
	private Compliance compliance;
	private Boolean ignoreUnusedResource;
	private LogLevel logLevel;
	private ResultLevel resultLevel;

	@Override
	public Compliance getCompliance() {
		return this.compliance;
	}

	@Override
	public LogLevel getLogLevel() {
		return this.logLevel;
	}

	@Override
	public ResultLevel getResultLevel() {
		return this.resultLevel;
	}

	@Override
	public Boolean isAllowCertificationSignatures() {
		return this.allowCertificationSignatures;
	}

	@Override
	public Boolean isIgnoreUnusedResource() {
		return this.ignoreUnusedResource;
	}

	@Override
	public PDFAValidationOptionSpec setAllowCertificationSignatures(boolean allowCertificationSignatures) {
		this.allowCertificationSignatures = allowCertificationSignatures;
		return this;
	}

	@Override
	public PDFAValidationOptionSpec setCompliance(Compliance compliance) {
		this.compliance = compliance; 
		return this;
	}

	@Override
	public PDFAValidationOptionSpec setIgnoreUnusedResource(boolean ignoreUnusedResource) {
		this.ignoreUnusedResource = ignoreUnusedResource;
		return this;
	}

	@Override
	public PDFAValidationOptionSpec setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	@Override
	public PDFAValidationOptionSpec setResultLevel(ResultLevel resultLevel) {
		this.resultLevel = resultLevel;
		return this;
	}

}
