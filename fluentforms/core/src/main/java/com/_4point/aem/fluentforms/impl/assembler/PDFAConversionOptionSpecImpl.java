package com._4point.aem.fluentforms.impl.assembler;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

public class PDFAConversionOptionSpecImpl implements PDFAConversionOptionSpec {

	private ColorSpace colorSpace;
	private Compliance compliance;
	private LogLevel logLevel;
	private List<Document> metadataSchemaExtensions;
	private OptionalContent optionalContent;
	private boolean removeInvalidXmpProperties = true;
	private ResultLevel resultLevel;
	private boolean retainPDFFormState;
	private Signatures signatures;
	private boolean verify = true;

	@Override
	public ColorSpace getColorSpace() {
		return colorSpace;
	}

	@Override
	public Compliance getCompliance() {
		return compliance;
	}

	@Override
	public LogLevel getLogLevel() {
		return logLevel;
	}

	@Override
	public List<Document> getMetadataSchemaExtensions() {
		return metadataSchemaExtensions;
	}

	@Override
	public OptionalContent getOptionalContent() {
		return optionalContent;
	}

	@Override
	public ResultLevel getResultLevel() {
		return resultLevel;
	}

	@Override
	public Signatures getSignatures() {
		return signatures;
	}

	@Override
	public boolean isRemoveInvalidXMPProperties() {
		return removeInvalidXmpProperties;
	}

	@Override
	public boolean isRetainPDFFormState() {
		return retainPDFFormState;
	}

	@Override
	public boolean isVerify() {
		return verify;
	}

	@Override
	public PDFAConversionOptionSpec setColorSpace(ColorSpace colorSpace) {
		this.colorSpace = colorSpace;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setCompliance(Compliance compliance) {
		this.compliance = compliance;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setMetadataSchemaExtensions(List<Document> metadataSchemaExtensions) {
		this.metadataSchemaExtensions = metadataSchemaExtensions;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setOptionalContent(OptionalContent optionalContent) {
		this.optionalContent = optionalContent;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setRemoveInvalidXMPProperties(boolean remove) {
		this.removeInvalidXmpProperties = remove;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setResultLevel(ResultLevel resultLevel) {
		this.resultLevel = resultLevel;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setRetainPDFFormState(boolean retainPDFFormState) {
		this.retainPDFFormState = retainPDFFormState;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setSignatures(Signatures signatures) {
		this.signatures = signatures;
		return this;
	}

	@Override
	public PDFAConversionOptionSpec setVerify(boolean verify) {
		this.verify = verify;
		return this;
	}

}
