package com._4point.aem.fluentforms.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.xmlunit.matchers.CompareMatcher;

import com._4point.aem.fluentforms.api.Xci;

class XciImplTest {
	
	private XciImpl.XciBuilderImpl builder = new XciImpl.XciBuilderImpl();

	private static final String XCI_FORMAT_STR = """
			<?xml version="1.0" encoding="UTF-8"?>
			<xdp:xdp xmlns:xdp="http://ns.adobe.com/xdp/">
				<config xmlns="http://www.xfa.org/schema/xci/1.0/"
					    xmlns:xfa="http://www.xfa.org/schema/xci/1.0/">
						%s
				</config>
			</xdp:xdp>
			""";
	
	private static final String XCI_FORMAT_STR_NO_DESTINATIONS = """
			<?xml version="1.0" encoding="UTF-8"?>
			<xdp:xdp xmlns:xdp="http://ns.adobe.com/xdp/">
			    <config xmlns="http://www.xfa.org/schema/xci/1.0/"
			            xmlns:xfa="http://www.xfa.org/schema/xci/1.0/">
			        <!-- No destinations defined -->
			    </config>
			</xdp:xdp>
			""";

	private static final String XCI_DESTINATION_FORMAT_STR = """
			<present> <!-- [0..n] -->
				<destination>%s</destination> <!-- pdf|ps|pcl -->
				<%s> <!-- [0..n] -->
					<fontInfo>
						<embed>%s</embed> <!-- 0|1 -->
					</fontInfo>
				</%s>
			</present>
			""";

	private static void compare(Xci xci, String expectedXml) throws IOException {
        String xciDoc = new String(xci.toDocument().getInputStream().readAllBytes());
        assertThat(xciDoc, CompareMatcher.isIdenticalTo(expectedXml).ignoreWhitespace().ignoreComments());
    }

	@Test
	void testToDocument_NoDestinations() throws Exception {
		Xci xci = builder.build();
		compare(xci, XCI_FORMAT_STR_NO_DESTINATIONS);
	}

	@Test
	void testToDocument_Pdf() throws Exception {
		Xci xci = builder.pdf()
							.embedFonts(true)
							.buildDestination()
						 .build();
		compare(xci, XCI_FORMAT_STR.formatted(pdfDestinationXml()));
	}

	private static String pdfDestinationXml() {
		return XCI_DESTINATION_FORMAT_STR.formatted("pdf", "pdf", "1", "pdf");
	}

	@Test
	void testToDocument_Pcl() throws Exception {
		Xci xci = builder.pcl()
							  .embedFonts(true)
							  .buildDestination()
						  .build();
		compare(xci, XCI_FORMAT_STR.formatted(pclDestinationXml()));
	}

	private static String pclDestinationXml() {
		return XCI_DESTINATION_FORMAT_STR.formatted("pcl", "pcl", "1", "pcl");
	}

	@Test
	void testToDocument_Ps() throws Exception {
		Xci xci = builder.ps()
							.embedFonts(true)
							.buildDestination()
						  .build();
		compare(xci, XCI_FORMAT_STR.formatted(psDestinationXml()));
	}

	private static String psDestinationXml() {
		return XCI_DESTINATION_FORMAT_STR.formatted("ps", "ps", "1", "ps");
	}

	// NOTE: To be honest, I'm not sure if the expected result is correct here, but since I don't have a valid XCI spec to refer to, I'm going with this.
	@Test
	void testToDocument_AllDestinations() throws Exception {
		Xci xci = builder.pdf()
							.embedFonts(true)
							.buildDestination()
						 .pcl()
						 	.embedFonts(true)
						 	.buildDestination()
						 .ps()
							.embedFonts(true)
							.buildDestination()
						 .build();
		compare(xci, XCI_FORMAT_STR.formatted(pdfDestinationXml() + pclDestinationXml() +  psDestinationXml()));
	}
}
