package com._4point.aem.fluentforms.impl.assembler;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.EitherDocumentOrDocumentList;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.OperationException;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class AssemblerServiceImplTest {
	
	@Mock
	private TraditionalDocAssemblerService adobeAssemblerService;

	private AssemblerService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = new AssemblerServiceImpl(adobeAssemblerService, UsageContext.SERVER_SIDE);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test invoke(Document,...) Happy Path.")
	void testInvoke() throws Exception {
		MockPdfAssemblerServiceInvoke svc = new MockPdfAssemblerServiceInvoke();
		
		Document ddx = Mockito.mock(Document.class);
		AssemblerOptionsSpec options = Mockito.mock(AssemblerOptionsSpec.class);
		Map<String, Object> sourceDocuments = Mockito.mock(Map.class);
		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, options);
		
		// Verify that all the results are correct.
		assertEquals(ddx, svc.getDdxOrg(), "Expected the ddx passed to AEM would match the ddx used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the AssemblerOptionsSpec passed to AEM would match the AssemblerOptionsSpec used.");
		assertTrue(svc.getSourceDocs() == sourceDocuments, "Expected the SourceDocuments passed to AEM would match the SourceDocuments used.");
		assertTrue(result == svc.getAssemblerResult(), "Expected the AssemblerResult returned by AEM would match the AssemblerResult.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test tesInvoke(Document,...) null arguments.")
	void tesInvoke_nullArguments() throws Exception {
		Document ddx = Mockito.mock(Document.class);
		AssemblerOptionsSpec options = Mockito.mock(AssemblerOptionsSpec.class);
		Map<String, Object> sourceDocuments = Mockito.mock(Map.class);
		Document nullDocument = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.invoke(nullDocument, sourceDocuments, options));
		assertTrue(ex1.getMessage().contains("ddx"), ()->"'" + ex1.getMessage() + "' does not contain 'ddx'");
        
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.invoke(ddx, null, options));
		assertTrue(ex2.getMessage().contains("sourceDocuments"), ()->"'" + ex2.getMessage() + "' does not contain 'sourceDocuments'");
	}
  
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test Invoke(Document,...) throws AssemblerServiceException.")
	void tesInvoke__AssemblerExceptionThrown() throws Exception {
		Mockito.when(adobeAssemblerService.invoke(Mockito.any(Document.class), Mockito.any(), Mockito.any())).thenThrow(AssemblerServiceException.class);

		Document ddx = Mockito.mock(Document.class);
		AssemblerOptionsSpec options = Mockito.mock(AssemblerOptionsSpec.class);
		Map<String, Object> sourceDocuments = Mockito.mock(Map.class);
		
		assertThrows(AssemblerServiceException.class, ()->underTest.invoke(ddx, sourceDocuments, options));
	}
	

	@SuppressWarnings("unchecked")
	@Test
	void testAssembleDocument_deprecated() throws Exception {
	    MockPdfAssemblerServiceInvoke svc = new MockPdfAssemblerServiceInvoke();
		Document ddx = Mockito.mock(Document.class);
		Map<String, Object> sourceDocuments = Mockito.mock(Map.class);
		@SuppressWarnings("deprecation")
		AssemblerResult result = underTest.invoke().setFailOnError(false)
				                                   .setDefaultStyle("abc")
				                                   .setLogLevel(LogLevel.CONFIG)
				                                   .setFirstBatesNumber(0)
				                                   .setTakeOwnership(true)
				                                   .setValidateOnly(true)
				                                   .executeOn(ddx, sourceDocuments);
		
		
		// Verify that all the results are correct.
		assertEquals(ddx, svc.getDdxOrg(), "Expected the ddx passed to AEM would match the ddx used.");
		assertSame(sourceDocuments, svc.getSourceDocs(), "Expected the SourceDocuments passed to AEM would match the SourceDocuments used.");
		assertSame(result, svc.getAssemblerResult(), "Expected the AssemblerResult returned by AEM would match the AssemblerResult.");
		AssemblerOptionSpecTest.assertNotEmpty(svc.getOptionsArg());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testAssembleDocument() throws Exception {
	    MockPdfAssemblerServiceInvoke svc = new MockPdfAssemblerServiceInvoke();
		Document ddx = Mockito.mock(Document.class);
		String sourceDocument1Name = "Name1";
		Document sourceDocument1 = Mockito.mock(Document.class);
		String sourceDocument2Name = "Name2";
		Document sourceDocument2 = Mockito.mock(Document.class);
		String sourceList1Name = "Name3";
		List<Document> sourceList1 = Mockito.mock(List.class);
		String sourceDocOrList1Name = "Name4";
		Document sourceDocument3 = Mockito.mock(Document.class);
		EitherDocumentOrDocumentList sourceDocOrList1 = EitherDocumentOrDocumentList.from(sourceDocument3);
		AssemblerResult result = underTest.invoke().setFailOnError(false)
				                                   .setDefaultStyle("abc")
				                                   .setLogLevel(LogLevel.CONFIG)
				                                   .setFirstBatesNumber(0)
				                                   .setTakeOwnership(true)
				                                   .setValidateOnly(true)
				                                   .add(sourceDocument1Name, sourceDocument1)
				                                   .add(sourceDocument2Name, sourceDocument2)
				                                   .add(sourceList1Name, sourceList1)
				                                   .add(sourceDocOrList1Name, sourceDocOrList1)
				                                   .executeOn(ddx);
		
		
		// Verify that all the results are correct.
		assertEquals(ddx, svc.getDdxOrg(), "Expected the ddx passed to AEM would match the ddx used.");
		Map<String, Object> sourceDocs = svc.getSourceDocs();
		assertThat(sourceDocs, hasEntry(sourceDocument1Name, sourceDocument1));
		assertThat(sourceDocs, hasEntry(sourceDocument2Name, sourceDocument2));
		assertThat(sourceDocs, hasEntry(sourceList1Name, sourceList1));
		assertThat(sourceDocs, hasEntry(sourceDocOrList1Name, sourceDocument3));
		assertThat(sourceDocs, aMapWithSize(4));	// Ensure those are the only entries.
		assertSame(result, svc.getAssemblerResult(), "Expected the AssemblerResult returned by AEM would match the AssemblerResult.");
		AssemblerOptionSpecTest.assertNotEmpty(svc.getOptionsArg());
	}
	
	private class MockPdfAssemblerServiceInvoke {
		private final AssemblerResult assemblerResult =  Mockito.mock(AssemblerResult.class);
		private final ArgumentCaptor<Document> ddx = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<AssemblerOptionsSpec> optionsArg = ArgumentCaptor.forClass(AssemblerOptionsSpec.class);
		@SuppressWarnings("unchecked")
		private final ArgumentCaptor<Map<String, Object>> sourceDocuments = ArgumentCaptor.forClass(Map.class);
		
		protected MockPdfAssemblerServiceInvoke() throws AssemblerServiceException, OperationException {
			super();
			// These are "lenient" because we only expect one or the other to be called.  Also, in some of the exceptional cases,
			// neither are called.
			Mockito.lenient().when(adobeAssemblerService.invoke(ddx.capture(),sourceDocuments.capture(), optionsArg.capture())).thenReturn(assemblerResult);
		
		}
		protected Document getDdxOrg() {
			return ddx.getValue();
		}

        protected AssemblerResult getAssemblerResult() {
        	return assemblerResult;
        }
        
		protected AssemblerOptionsSpec getOptionsArg() {
			return optionsArg.getValue();
		}
		
		protected Map<String, Object> getSourceDocs() {
        	return sourceDocuments.getValue();
        }
        
	}

	@Test
	@DisplayName("Test isPDFA(Document,ValidationOptions) Happy Path.")
	void testIsPdfA() throws Exception {
		// Given 
//		PDFAValidationOptionSpec validationOptions;
//		Document pdf;
//		
//		// When
//		PDFAValidationResult result = underTest.isPDFA(pdf, validationOptions);
//		
		// Then
		
//		MockPdfAssemblerServiceInvoke svc = new MockPdfAssemblerServiceInvoke();
//		
//		Document ddx = Mockito.mock(Document.class);
//		AssemblerOptionsSpec options = Mockito.mock(AssemblerOptionsSpec.class);
//		Map<String, Object> sourceDocuments = Mockito.mock(Map.class);
//		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, options);
//		
//		// Verify that all the results are correct.
//		assertEquals(ddx, svc.getDdxOrg(), "Expected the ddx passed to AEM would match the ddx used.");
//		assertTrue(svc.getOptionsArg() == options, "Expected the AssemblerOptionsSpec passed to AEM would match the AssemblerOptionsSpec used.");
//		assertTrue(svc.getSourceDocs() == sourceDocuments, "Expected the SourceDocuments passed to AEM would match the SourceDocuments used.");
//		assertTrue(result == svc.getAssemblerResult(), "Expected the AssemblerResult returned by AEM would match the AssemblerResult.");
	}
	
}
