package com._4point.aem.fluentforms.api.output;

import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.output.api.AcrobatVersion;

public interface PDFOutputOptionsSetter {

	PDFOutputOptionsSetter setAcrobatVersion(AcrobatVersion acrobatVersion);

	PDFOutputOptionsSetter setContentRoot(PathOrUrl contentRoot);

	default PDFOutputOptionsSetter setContentRoot(Path contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

	default PDFOutputOptionsSetter setContentRoot(URL contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

	PDFOutputOptionsSetter setDebugDir(Path debugDir);

	PDFOutputOptionsSetter setEmbedFonts(boolean embedFonts);

	PDFOutputOptionsSetter setLinearizedPDF(boolean linearizedPDF);

	PDFOutputOptionsSetter setLocale(Locale locale);

	PDFOutputOptionsSetter setRetainPDFFormState(boolean retainFormState);

	PDFOutputOptionsSetter setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields);

	PDFOutputOptionsSetter setTaggedPDF(boolean isTagged);

	PDFOutputOptionsSetter setXci(Document xci);

}