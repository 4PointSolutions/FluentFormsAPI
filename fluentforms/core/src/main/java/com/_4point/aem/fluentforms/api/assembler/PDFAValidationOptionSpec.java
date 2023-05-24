package com._4point.aem.fluentforms.api.assembler;

import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel;

public interface PDFAValidationOptionSpec extends PDFAValidationOptionsSetter {

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#getCompliance()
	 */
	public Compliance getCompliance();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#getLogLevel()
	 */
	public LogLevel getLogLevel();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#getResultLevel()
	 */
	public ResultLevel getResultLevel();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#isAllowCertificationSignatures()
	 */
	public Boolean isAllowCertificationSignatures();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#isIgnoreUnusedResource()
	 */
	public Boolean isIgnoreUnusedResource();

	/**
	 * @param allowCertificationSignatures
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setAllowCertificationSignatures(boolean)
	 */
	public PDFAValidationOptionSpec setAllowCertificationSignatures(boolean allowCertificationSignatures);

	/**
	 * @param compliance
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setCompliance(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance)
	 */
	public PDFAValidationOptionSpec setCompliance(Compliance compliance);

	/**
	 * @param ignoreUnusedResource
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setIgnoreUnusedResource(boolean)
	 */
	public PDFAValidationOptionSpec setIgnoreUnusedResource(boolean ignoreUnusedResource);

	/**
	 * @param logLevel
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setLogLevel(java.lang.String)
	 */
	public PDFAValidationOptionSpec setLogLevel(LogLevel logLevel);

	/**
	 * @param resultLevel
	 * @see com.adobe.fd.assembler.client.PDFAValidationOptionSpec#setResultLevel(com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel)
	 */
	public PDFAValidationOptionSpec setResultLevel(ResultLevel resultLevel);
}
