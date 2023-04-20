package com._4point.aem.fluentforms.api.assembler;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

public interface PDFAConversionOptionsSetter {

	PDFAConversionOptionsSetter setColorSpace(ColorSpace colorSpace);

	PDFAConversionOptionsSetter setCompliance(Compliance compliance);

	PDFAConversionOptionsSetter setLogLevel(LogLevel logLevel);

	PDFAConversionOptionsSetter setMetadataSchemaExtensions(List<Document> metadataSchemaExtensions);

	PDFAConversionOptionsSetter setOptionalContent(OptionalContent optionalContent);

	PDFAConversionOptionsSetter setRemoveInvalidXMPProperties(boolean remove);

	PDFAConversionOptionsSetter setResultLevel(ResultLevel resultLevel);

	PDFAConversionOptionsSetter setRetainPDFFormState(boolean retainPDFFormState);

	PDFAConversionOptionsSetter setSignatures(Signatures signatures);

	PDFAConversionOptionsSetter setVerify(boolean verify);
}
