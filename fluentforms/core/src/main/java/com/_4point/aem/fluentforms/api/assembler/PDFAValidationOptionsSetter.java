package com._4point.aem.fluentforms.api.assembler;

import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel;

public interface PDFAValidationOptionsSetter {
	/**
	 * @param allowCertificationSignatures
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setAllowCertificationSignatures(boolean)
	 */
	public PDFAValidationOptionsSetter setAllowCertificationSignatures(boolean allowCertificationSignatures);

	/**
	 * @param compliance
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setCompliance(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance)
	 */
	public PDFAValidationOptionsSetter setCompliance(Compliance compliance);

	/**
	 * @param ignoreUnusedResource
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setIgnoreUnusedResource(boolean)
	 */
	public PDFAValidationOptionsSetter setIgnoreUnusedResource(boolean ignoreUnusedResource);

	/**
	 * @param logLevel
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setLogLevel(java.lang.String)
	 */
	public PDFAValidationOptionsSetter setLogLevel(LogLevel logLevel);

	/**
	 * @param resultLevel
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setResultLevel(com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel)
	 */
	public PDFAValidationOptionsSetter setResultLevel(ResultLevel resultLevel);
}
