package com._4point.aem.fluentforms.api.output;

import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.output.api.AcrobatVersion;

public interface PDFOutputOptionsSetter {

	PDFOutputOptions setAcrobatVersion(AcrobatVersion acrobatVersion);

	PDFOutputOptions setContentRoot(PathOrUrl contentRoot);

	default PDFOutputOptions setContentRoot(Path contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

	default PDFOutputOptions setContentRoot(URL contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

	PDFOutputOptions setDebugDir(Path debugDir);

	PDFOutputOptions setEmbedFonts(boolean embedFonts);

	PDFOutputOptions setLinearizedPDF(boolean linearizedPDF);

	PDFOutputOptions setLocale(Locale locale);

	PDFOutputOptions setRetainPDFFormState(boolean retainFormState);

	PDFOutputOptions setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields);

	PDFOutputOptions setTaggedPDF(boolean isTagged);

	PDFOutputOptions setXci(Document xci);

}