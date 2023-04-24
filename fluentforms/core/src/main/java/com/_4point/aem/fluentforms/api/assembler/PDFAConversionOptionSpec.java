package com._4point.aem.fluentforms.api.assembler;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

public interface PDFAConversionOptionSpec extends PDFAConversionOptionsSetter {
	
	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getColorSpace()
	 */
	public ColorSpace getColorSpace();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getCompliance()
	 */
	public Compliance getCompliance();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getLogLevel()
	 */
	public LogLevel getLogLevel();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getMetadataSchemaExtensions()
	 */
	public List<Document> getMetadataSchemaExtensions();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getOptionalContent()
	 */
	public OptionalContent getOptionalContent();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getResultLevel()
	 */
	public ResultLevel getResultLevel();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#getSignatures()
	 */
	public Signatures getSignatures();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#isRemoveInvalidXMPProperties()
	 */
	public Boolean isRemoveInvalidXMPProperties();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#isRetainPDFFormState()
	 */
	public Boolean isRetainPDFFormState();

	/**
	 * @return
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#isVerify()
	 */
	public Boolean isVerify();

	/**
	 * @param colorSpace
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setColorSpace(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace)
	 */
	public PDFAConversionOptionSpec setColorSpace(ColorSpace colorSpace);

	/**
	 * @param compliance
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setCompliance(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance)
	 */
	public PDFAConversionOptionSpec setCompliance(Compliance compliance);

	/**
	 * @param logLevel
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setLogLevel(java.lang.String)
	 */
	public PDFAConversionOptionSpec setLogLevel(LogLevel logLevel);

	/**
	 * @param metadataSchemaExtensions
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setMetadataSchemaExtensions(java.util.List)
	 */
	public PDFAConversionOptionSpec setMetadataSchemaExtensions(List<Document> metadataSchemaExtensions);

	/**
	 * @param optionalContent
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setOptionalContent(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent)
	 */
	public PDFAConversionOptionSpec setOptionalContent(OptionalContent optionalContent);

	/**
	 * @param remove
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setRemoveInvalidXMPProperties(boolean)
	 */
	public PDFAConversionOptionSpec setRemoveInvalidXMPProperties(boolean remove);

	/**
	 * @param resultLevel
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setResultLevel(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel)
	 */
	public PDFAConversionOptionSpec setResultLevel(ResultLevel resultLevel);

	/**
	 * @param retainPDFFormState
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setRetainPDFFormState(boolean)
	 */
	public PDFAConversionOptionSpec setRetainPDFFormState(boolean retainPDFFormState);

	/**
	 * @param signatures
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setSignatures(com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures)
	 */
	public PDFAConversionOptionSpec setSignatures(Signatures signatures);

	/**
	 * @param verify
	 * @see com.adobe.fd.assembler.client.PDFAConversionOptionSpec#setVerify(boolean)
	 */
	public PDFAConversionOptionSpec setVerify(boolean verify);
	
}
