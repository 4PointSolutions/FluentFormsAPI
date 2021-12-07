package com._4point.aem.fluentforms.impl.forms;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;

class AdobeFormsServiceAdapterTest {

	// Test the the code converts local FileSystem locations from relative to absolute
	// See issue #31 for why we did this.
	@Test
	void testtoAdobePDFOutputOptions_RelPath() {
		PDFFormRenderOptions options = new PDFFormRenderOptionsImpl();
		options.setContentRoot(Paths.get("foo", "bar"));
		com.adobe.fd.forms.api.PDFFormRenderOptions result = AdobeFormsServiceAdapter.toAdobePDFFormRenderOptions(options);
		assertFalse(PathOrUrl.from(result.getContentRoot()).isRelative());	// Make sure the result is no longer relative
	}

}
