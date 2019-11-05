package com._4point.aem.fluentforms.api.output;

import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.output.api.AcrobatVersion;

public interface PDFOutputOptions extends PDFOutputOptionsSetter {

	AcrobatVersion getAcrobatVersion();

	PathOrUrl getContentRoot();

	Path getDebugDir();

	Boolean getEmbedFonts();

	Boolean getLinearizedPDF();

	Locale getLocale();

	Boolean getRetainPDFFormState();

	Boolean getRetainUnsignedSignatureFields();

	Boolean getTaggedPDF();

	Document getXci();
}