package com._4point.aem.fluentforms.api.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerArgumentBuilder;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.EitherDocumentOrDocumentList;
import com.adobe.fd.assembler.client.OperationException;

@ExtendWith(MockitoExtension.class)
class AssemblerServiceTest {

	@Mock Document doc1;
	@Mock Document doc2;
	@Mock Document doc3;
	@Mock Document doc4;
	@Mock Document doc5;
	@Mock Document doc6;

	List<Document> testList1 = Arrays.asList(doc1, doc2, doc3);
	List<Document> testList2 = Arrays.asList(doc4, doc5, doc6);
	
	MockAssemblerArgumentBuilder underTest = new MockAssemblerArgumentBuilder();

	@Test
	void AssemblerArgumentBuilder_addAllDocs() {
		// given
		Stream<Entry<String, Document>> stream = IntStream
			      .range(0, testList1.size())
			      .mapToObj(i->new SimpleEntry<>("Name" + i, testList1.get(i)))
				;

		// when
		underTest.addAllDocs(stream);

		// then
		List<Entry<String, Document>> docsAdded = underTest.getDocsAdded();
		for (int i = 0; i < docsAdded.size(); i++) {
			Entry<String, Document> docEntry = docsAdded.get(i);
			assertEquals("Name" + i, docEntry.getKey());
			assertSame(testList1.get(i), docEntry.getValue());
		}
	}

	@Test
	void AssemblerArgumentBuilder_addAllListss() {
		// given
		Entry<String, List<Document>> entry1 = new SimpleEntry<>("Name0", testList1);
		Entry<String, List<Document>> entry2 = new SimpleEntry<>("Name1", testList2);
				;
		// when
		underTest.addAllLists(Stream.of(entry1, entry2));

		// then
		List<Entry<String, List<Document>>> listsAdded = underTest.getListsAdded();
		for (int i = 0; i < listsAdded.size(); i++) {
			Entry<String, List<Document>> listEntry = listsAdded.get(i);
			assertEquals("Name" + i, listEntry.getKey());
			assertSame(i == 0 ? testList1 : testList2, listEntry.getValue());
		}
		
	}

	@Test
	void AssemblerArgumentBuilder_addAll() {
		// given
		EitherDocumentOrDocumentList value1 = EitherDocumentOrDocumentList.from(doc1);
		EitherDocumentOrDocumentList value2 = EitherDocumentOrDocumentList.from(testList2);
		EitherDocumentOrDocumentList value3 = EitherDocumentOrDocumentList.from(doc2);
		Entry<String, EitherDocumentOrDocumentList> entry1 = new SimpleEntry<>("Name0", value1);
		Entry<String, EitherDocumentOrDocumentList> entry2 = new SimpleEntry<>("Name1", value2);
		Entry<String, EitherDocumentOrDocumentList> entry3 = new SimpleEntry<>("Name2", value3);

		// when
		underTest.addAll(Stream.of(entry1, entry2, entry3));
		
		// then
		List<Entry<String, EitherDocumentOrDocumentList>> eithersAdded = underTest.getEithersAdded();
		for (int i = 0; i < eithersAdded.size(); i++) {
			Entry<String, EitherDocumentOrDocumentList> listEntry = eithersAdded.get(i);
			assertEquals("Name" + i, listEntry.getKey());
			assertSame(i == 0 ? value1 : i == 1 ? value2 : value3, listEntry.getValue());
		}
	}

	private static class MockAssemblerArgumentBuilder  implements AssemblerArgumentBuilder {
		private final List<Map.Entry<String, Document>> docsAdded = new ArrayList<>();
		private final List<Map.Entry<String, List<Document>>> listsAdded = new ArrayList<>();
		private final List<Map.Entry<String, EitherDocumentOrDocumentList>> eithersAdded = new ArrayList<>();

		@Override
		public AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder setDefaultStyle(String defaultStyle) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder setFirstBatesNumber(int start) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder setLogLevel(LogLevel logLevel) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder setTakeOwnership(Boolean takeOwnership) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder setValidateOnly(Boolean validateOnly) {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerArgumentBuilder add(String name, Document document) {
			this.docsAdded.add(new SimpleEntry<>(name, document));
			return this;
		}

		@Override
		public AssemblerArgumentBuilder add(String name, List<Document> documentList) {
			this.listsAdded.add(new SimpleEntry<>(name, documentList));
			return this;
		}

		@Override
		public AssemblerArgumentBuilder add(String name, EitherDocumentOrDocumentList docOrList) {
			this.eithersAdded.add(new SimpleEntry<>(name, docOrList));
			return this;
		}

		@Override
		public AssemblerResult executeOn(Document ddx, Map<String, Object> sourceDocuments)
				throws AssemblerServiceException, OperationException {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerResult executeOn2(Document ddx, Map<String, EitherDocumentOrDocumentList> sourceDocuments)
				throws AssemblerServiceException, OperationException {
			throw new UnsupportedOperationException("method not implemented.");
		}

		@Override
		public AssemblerResult executeOn(Document ddx) throws AssemblerServiceException, OperationException {
			throw new UnsupportedOperationException("method not implemented.");
		}

		public List<Map.Entry<String, Document>> getDocsAdded() {
			return docsAdded;
		}

		public List<Map.Entry<String, List<Document>>> getListsAdded() {
			return listsAdded;
		}

		public List<Map.Entry<String, EitherDocumentOrDocumentList>> getEithersAdded() {
			return eithersAdded;
		}		
		
	}
}
