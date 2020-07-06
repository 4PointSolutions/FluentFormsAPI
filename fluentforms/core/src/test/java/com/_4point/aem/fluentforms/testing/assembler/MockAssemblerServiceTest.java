package com._4point.aem.fluentforms.testing.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;

public class MockAssemblerServiceTest {
	
	@SuppressWarnings("unchecked")
	@Test
	void testInvokeMockDocument() throws Exception {
		String expectedResultString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><DDX xmlns=\"http://ns.adobe.com/DDX/1.0/\">\r\n" + 
				"<PDF result=\"concatenatedPDF.pdf\"><PDF source=\"File0.pdf\"/>\r\n" + 
				"<PDF source=\"File1.pdf\"/></PDF></DDX>";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document ddx = mockDocumentFactory.create(expectedResultString.getBytes());
		Map<String,Object>sourceDocuments =  Mockito.mock(Map.class);
		
		AssemblerResult assemblerResult = new AssemblerResult() {
			
			@Override
			public Map<String, OperationException> getThrowables() {
				return null;
			}
			
			@Override
			public List<String> getSuccessfulDocumentNames() {			
				return null;
			}
			
			@Override
			public List<String> getSuccessfulBlockNames() {
				return null;
			}
			
			@Override
			public int getNumRequestedBlocks() {
				return 0;
			}
			
			@Override
			public Map<String, List<String>> getMultipleResultsBlocks() {
				return null;
			}
			
			@Override
			public int getLastBatesNumber() {			
				return 0;
			}
			
			@Override
			public List<String> getFailedBlockNames() {			
				return null;
			}
			
			@Override
			public Map<String, Document> getDocuments() {	
				return null;
			}
			
			@Override
			public Document etJobLog() {
				return null;
			}
		};
	    MockAssemblerService underTest = MockAssemblerService.createAssemblerResultMock(assemblerResult);
		AssemblerResult result = underTest.invoke()
				 .executeOn(ddx, sourceDocuments);		
		assertEquals(result, assemblerResult);
	}
	
}
