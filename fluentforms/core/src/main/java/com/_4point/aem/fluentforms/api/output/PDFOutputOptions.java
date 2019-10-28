package com._4point.aem.fluentforms.api.output;

import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;

public interface PDFOutputOptions extends PDFOutputOptionsSetter {

	boolean getLinearizedPDF();

	Locale getLocale();

	boolean getRetainPDFFormState();

	boolean getRetainUnsignedSignatureFields();

	boolean getTaggedPDF();

	Document getXci();
}