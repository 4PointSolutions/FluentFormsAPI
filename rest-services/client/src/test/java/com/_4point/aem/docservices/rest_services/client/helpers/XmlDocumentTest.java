package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument.XmlDocumentException;
import com._4point.aem.fluentforms.api.Document;

class XmlDocumentTest {

	private static final String PDFA_DOCUMENT_CONTENTS_1 = "PDFA Document Contents #1";
	private static final String PDFA_DOCUMENT_CONTENTS_2 = "PDFA Document Contents #2";
	private static final byte[] PDFA_DOCUMENT_BYTES_1 = "PDFA Document Bytes #1".getBytes(StandardCharsets.UTF_8);
	private static final byte[] PDFA_DOCUMENT_BYTES_2 = "PDFA Document Bytes #2".getBytes(StandardCharsets.UTF_8);
	private static final String JOB_LOG_DOCUMENT_CONTENTS = "JOB LOG Document Contents";
	private static final byte[] CONVERSION_LOG_DOCUMENT_CONTENTS = "Conversion Log Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final String SAMPLE_XML = "<ToPdfAResult>\n"
			+ "  <ConversionLog>" + Base64.getEncoder().encodeToString(CONVERSION_LOG_DOCUMENT_CONTENTS) + "</ConversionLog>\n"
			+ "  <JobLog>" + JOB_LOG_DOCUMENT_CONTENTS + "</JobLog>\n"
			+ "  <PdfADocument>" + PDFA_DOCUMENT_CONTENTS_1 + "</PdfADocument>\n"
			+ "  <PdfADocument>" + PDFA_DOCUMENT_CONTENTS_2 + "</PdfADocument>\n"
			+ "  <PdfADocumentBase64>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_BYTES_1) + "</PdfADocumentBase64>\n"
			+ "  <PdfADocumentBase64>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_BYTES_2) + "</PdfADocumentBase64>\n"
			+ "  <IsPdfA>true</IsPdfA>\n"
			+ "</ToPdfAResult>\n";

	private final XmlDocument underTest;
	
	public XmlDocumentTest() {
		try {
			this.underTest = XmlDocument.create(new ByteArrayInputStream(SAMPLE_XML.getBytes()));
		} catch (XmlDocumentException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	void testGetString() throws Exception {
		assertEquals(JOB_LOG_DOCUMENT_CONTENTS, underTest.getString("/ToPdfAResult/JobLog"));
	}

	@Test
	void testGetDocument() throws Exception {
		assertArrayEquals(CONVERSION_LOG_DOCUMENT_CONTENTS, IOUtils.toByteArray(underTest.getDocument("/ToPdfAResult/ConversionLog").getInputStream()));
	}

	@Test
	void testGetMultipleStrings() throws Exception {
		List<String> result = underTest.getStrings("/ToPdfAResult/PdfADocument");
		assertEquals(2, result.size());
		assertAll(
				()->assertEquals(PDFA_DOCUMENT_CONTENTS_1, result.get(0)),
				()->assertEquals(PDFA_DOCUMENT_CONTENTS_2, result.get(1))
				);
	}

	@Test
	void testGetMultipleDocuments() throws Exception {
		List<Document> result = underTest.getDocuments("/ToPdfAResult/PdfADocumentBase64");
		assertEquals(2, result.size());
		assertAll(
				()->assertArrayEquals(PDFA_DOCUMENT_BYTES_1, IOUtils.toByteArray(result.get(0).getInputStream())),
				()->assertArrayEquals(PDFA_DOCUMENT_BYTES_2, IOUtils.toByteArray(result.get(1).getInputStream()))
				);
	}

}
