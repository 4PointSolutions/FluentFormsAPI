package com._4point.aem.fluentforms.impl.output;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.AdobeOutputServiceAdapter.PrintConfigMapping;
import com.adobe.fd.output.api.RenderType;

class AdobeOutputServiceAdapterTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testPrintConfigMapping() {
		assertEquals(com.adobe.fd.output.api.PrintConfig.DPL300, PrintConfigMapping.from(PrintConfigImpl.DPL300).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.DPL406, PrintConfigMapping.from(PrintConfigImpl.DPL406).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.DPL600, PrintConfigMapping.from(PrintConfigImpl.DPL600).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.Generic_PS_L3, PrintConfigMapping.from(PrintConfigImpl.Generic_PS_L3).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.GenericColor_PCL_5c, PrintConfigMapping.from(PrintConfigImpl.GenericColor_PCL_5c).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.HP_PCL_5e, PrintConfigMapping.from(PrintConfigImpl.HP_PCL_5e).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.IPL300, PrintConfigMapping.from(PrintConfigImpl.IPL300).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.IPL400, PrintConfigMapping.from(PrintConfigImpl.IPL400).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.PS_PLAIN, PrintConfigMapping.from(PrintConfigImpl.PS_PLAIN).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.TPCL305, PrintConfigMapping.from(PrintConfigImpl.TPCL305).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.TPCL600, PrintConfigMapping.from(PrintConfigImpl.TPCL600).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.ZPL300, PrintConfigMapping.from(PrintConfigImpl.ZPL300).get());
		assertEquals(com.adobe.fd.output.api.PrintConfig.ZPL600, PrintConfigMapping.from(PrintConfigImpl.ZPL600).get());
		assertFalse(PrintConfigMapping.from(PrintConfigImpl.custom(PathOrUrl.from("foo"), RenderType.PCL)).isPresent());
	}

	@Test
	void testToAdobePrintConfigtOptions_PreExisting() {
		assertEquals(com.adobe.fd.output.api.PrintConfig.HP_PCL_5e, AdobeOutputServiceAdapter.toAdobePrintConfig(PrintConfigImpl.HP_PCL_5e));
	}

	@Test
	void testToAdobePrintConfigOptions_Custom() {
		final String testXci = "foo";
		final RenderType testRenderType = RenderType.PCL;
		com.adobe.fd.output.api.PrintConfig adobePrintConfig = AdobeOutputServiceAdapter.toAdobePrintConfig(PrintConfigImpl.custom(PathOrUrl.from(testXci), testRenderType));
		assertAll(
				()->assertEquals(testXci, adobePrintConfig.getXdcUri()),
				()->assertEquals(testRenderType, adobePrintConfig.getRenderType())
				);
	}

	@Test
	void testToAdobePrintConfigOptions_Null() {
		assertNull(AdobeOutputServiceAdapter.toAdobePrintConfig(null));
	}
	
	// Test the the code converts local FileSystem locations from relative to absolute
	// See issue #31 for why we did this.
	@Test
	void testtoAdobePDFOutputOptions_RelPath() {
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		options.setContentRoot(Paths.get("foo", "bar"));
		final com.adobe.fd.output.api.PDFOutputOptions result = AdobeOutputServiceAdapter.toAdobePDFOutputOptions(options);
		assertFalse(PathOrUrl.from(result.getContentRoot()).isRelative());	// Make sure the result is no longer relative
	}

	// Test the the code converts local FileSystem locations from relative to absolute
	// See issue #31 for why we did this.
	@Test
	void testtoAdobePrintedOutputOptions_RelPath() {
		PrintedOutputOptions options = new PrintedOutputOptionsImpl();
		options.setContentRoot(Paths.get("foo", "bar"));
		final com.adobe.fd.output.api.PrintedOutputOptions result = AdobeOutputServiceAdapter.toAdobePrintedOutputOptions(options);
		assertFalse(PathOrUrl.from(result.getContentRoot()).isRelative());	// Make sure the result is no longer relative
	}
}
