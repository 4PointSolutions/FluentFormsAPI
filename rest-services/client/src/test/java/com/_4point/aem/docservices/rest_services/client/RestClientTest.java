package com._4point.aem.docservices.rest_services.client;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload.Builder;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

class RestClientTest {

	
	private static final DocumentFactory DOC_FACTORY = SimpleDocumentFactoryImpl.getFactory();

	/**
	 * This class is used to test all the Builder interface's default methods.
	 */
	private static class MockBuilder implements Builder {
		record AddStringStringParams(String fieldName, String fieldData) {};
		record AddStringByteArrrayContentTypeParams(String fieldName, byte[] fieldData, ContentType contentType) {};
		record AddStringInputStreamContentTypeParams(String fieldName, InputStream fieldData, ContentType contentType) {};
		
		private List<AddStringStringParams> addStringStringParams = new ArrayList<>();
		private List<AddStringByteArrrayContentTypeParams> addStringByteArrrayContentTypeParams = new ArrayList<>();
		private List<AddStringInputStreamContentTypeParams> addStringInputStreamContentTypeParams = new ArrayList<>();

		@Override
		public Builder add(String fieldName, String fieldData) {
			addStringStringParams.add(new AddStringStringParams(fieldName, fieldData));
			return this;
		}

		@Override
		public Builder add(String fieldName, byte[] fieldData, ContentType contentType) {
			addStringByteArrrayContentTypeParams.add(new AddStringByteArrrayContentTypeParams(fieldName, fieldData, contentType));
			return this;
		}

		@Override
		public Builder add(String fieldName, InputStream fieldData, ContentType contentType) {
			addStringInputStreamContentTypeParams.add(new AddStringInputStreamContentTypeParams(fieldName, fieldData, contentType));
			return this;
		}

		@Override
		public Builder queryParam(String name, String value) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public Builder addHeader(String name, String value) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public MultipartPayload build() {
			throw new UnsupportedOperationException("Not implemented");
		}
	};

	private final MockBuilder underTest = new MockBuilder();
	
	enum InputStreamTest {
		NonNullInputStream,
		NonNullDocWithoutContentType, 
		NonNullDocWithContentType,
		DocWithoutContentType, 
		DocWithContentType;
	}

	@ParameterizedTest
	@EnumSource
	void testAddInputStreamVariations(InputStreamTest testType) {
		// given
		String fieldName = "fieldName";
		byte[] fieldData = "fieldData".getBytes();
		String contentType = "application/pdf";
		Document doc = DOC_FACTORY.create(fieldData);
		
		
		// when
		switch (testType) {
			case NonNullInputStream -> underTest.addIfNotNull(fieldName, new ByteArrayInputStream(fieldData), ContentType.APPLICATION_PDF);
			case NonNullDocWithoutContentType -> { doc.setContentType(contentType); underTest.addIfNotNull(fieldName, doc); }	// Set the content type of the document
			case NonNullDocWithContentType -> underTest.addIfNotNull(fieldName, doc, ContentType.APPLICATION_PDF);				
			case DocWithoutContentType -> { doc.setContentType(contentType); underTest.add(fieldName, doc); }  	// Set the content type of the document
			case DocWithContentType -> underTest.add(fieldName, doc, ContentType.APPLICATION_PDF);				 // Explicitly pass in the content type
		}
		
		// then
		assertEquals(1, underTest.addStringInputStreamContentTypeParams.size());
		MockBuilder.AddStringInputStreamContentTypeParams params = underTest.addStringInputStreamContentTypeParams.get(0);
		assertAll(
				()->assertEquals(fieldName, params.fieldName()),
				()->assertArrayEquals(fieldData, readInputStream(params.fieldData())),
				()->assertEquals(contentType, params.contentType().contentType())
				);
	}


	enum CollectionTest {
		List, Stream, NonNullList;
	}

	@ParameterizedTest
	@EnumSource
	void testAddStringCollections(CollectionTest testType) {
		// given
		String fieldName = "fieldName";
		List<String> fieldData = List.of("fieldData1", "fieldData2");
		
		// when
		switch (testType) {
			case List -> underTest.addStrings(fieldName, fieldData);
			case Stream -> underTest.addStrings(fieldName, fieldData.stream());
			case NonNullList -> underTest.addStringVersion(fieldName, fieldData);
		}
		
		// then
		assertEquals(fieldData.size(), underTest.addStringStringParams.size());
		
		for (int i = 0; i < fieldData.size(); i++) {
			int index = i;	// Need to use final variable in lambda
			MockBuilder.AddStringStringParams params = underTest.addStringStringParams.get(index);
			assertAll(
					() -> assertEquals(fieldName, params.fieldName()),
					() -> assertEquals(fieldData.get(index), params.fieldData())
					);
		}
	}

	@ParameterizedTest
	@EnumSource
	void testAddDocsCollections(CollectionTest testType) {
		// given
		String fieldName = "fieldName";
		String contentType = "application/pdf";
		List<byte[]> rawFieldData = List.of("fieldData1".getBytes(), "fieldData2".getBytes());
		List<Document> fieldData = rawFieldData.stream()
                							   .map(ba->DOC_FACTORY.create(ba))
                							   .map(doc -> doc.setContentType(contentType))
                							   .toList();
		
		// when
		switch (testType) {
			case List -> underTest.addDocs(fieldName, fieldData);				// Test the List version
			case Stream -> underTest.addDocs(fieldName, fieldData.stream());	// Test the Stream version
			case NonNullList -> underTest.addDocsIfNotNull(fieldName, fieldData);
		}
		
		// then
		assertEquals(fieldData.size(), underTest.addStringInputStreamContentTypeParams.size());
		
		for (int i = 0; i < fieldData.size(); i++) {
			int index = i;	// Need to use final variable in lambda
			MockBuilder.AddStringInputStreamContentTypeParams params = underTest.addStringInputStreamContentTypeParams.get(index);
			assertAll(
					()->assertEquals(fieldName, params.fieldName()),
					()->assertArrayEquals(rawFieldData.get(index), readInputStream(params.fieldData())),
					()->assertEquals(contentType, params.contentType().contentType())
					);
		}
	}

	@Test
	void testAddByteArrayVariarions() {
		// given
		String fieldName = "fieldName";
		byte[] fieldData = "fieldData1".getBytes();
		
		// when
		underTest.addIfNotNull(fieldName, fieldData, ContentType.APPLICATION_PDF);
		
		// then
		assertEquals(1, underTest.addStringByteArrrayContentTypeParams.size());
		MockBuilder.AddStringByteArrrayContentTypeParams params = underTest.addStringByteArrrayContentTypeParams.get(0);
		assertAll(
				() -> assertEquals(fieldName, params.fieldName()),
				() -> assertArrayEquals(fieldData, params.fieldData()),
				() -> assertEquals(ContentType.APPLICATION_PDF, params.contentType())
				);
	}

	@Test
	void testAddIfNotNull_Null() {
		// Test all the addifNotNull cases when null is used.
		// Given
		String fieldName = "fieldName";
		
		// When
		underTest.addIfNotNull(fieldName, (String) null);
		underTest.addIfNotNull(fieldName, (byte[]) null, ContentType.APPLICATION_PDF);
		underTest.addIfNotNull(fieldName, (InputStream) null, ContentType.APPLICATION_PDF);
		underTest.addIfNotNull(fieldName, (Document) null);
		underTest.addIfNotNull(fieldName, (Document) null, ContentType.APPLICATION_PDF);
		underTest.addDocsIfNotNull(fieldName, (List<Document>) null);
		underTest.addStringVersion(fieldName, (String) null);
		underTest.addStringVersion(fieldName, (List<Document>) null);
		underTest.transformAndAdd(fieldName, (String) null, s -> s);	// Initial Value is null
		underTest.transformAndAdd(fieldName, "", s -> null);			// Value after transformation is null
		underTest.transformAndAddBytes(fieldName, (String) null, ContentType.APPLICATION_PDF, s -> s.getBytes()); // Initial Value is null
		underTest.transformAndAddBytes(fieldName, "".getBytes(), ContentType.APPLICATION_PDF, s -> null); // Value after transformation is null
		underTest.transformAndAddInputStream(fieldName, (String) null, ContentType.APPLICATION_PDF, s -> new ByteArrayInputStream(s.getBytes())); // Initial Value is null
		underTest.transformAndAddInputStream(fieldName, new ByteArrayInputStream("".getBytes()), ContentType.APPLICATION_PDF, s -> null); // Value after transformation is null
		underTest.transformAndAddStringVersion(fieldName, (String) null, s -> s); // Initial Value is null
		underTest.transformAndAddStringVersion(fieldName, "", s -> null); // Value after transformation is null
		underTest.transformAndAddDocs(fieldName, (String) null, s -> { throw new IllegalArgumentException("Shouldn't reach here"); }); // Initial Value is null
		underTest.transformAndAddDocs(fieldName, "", s -> null); // Value after transformation is null
		
		assertAll(
				()->assertTrue(underTest.addStringInputStreamContentTypeParams.isEmpty(), "InputStream was added when null was passed in."),
				()->assertTrue(underTest.addStringStringParams.isEmpty(), "String was added when null was passed in."),
				()->assertTrue(underTest.addStringByteArrrayContentTypeParams.isEmpty(), "byte[] was added when null was passed in.")
				);
		
	}

	private byte[] readInputStream(InputStream is) {
		try {
			return is.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
