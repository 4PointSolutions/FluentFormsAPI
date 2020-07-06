package com._4point.aem.fluentforms.api.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
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
	@DisplayName("Test testInvoke(Document,...) Happy Path.")
	void testInvoke() throws Exception {
		MockPdfAssemblerService svc = new MockPdfAssemblerService();
		
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
        
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.invoke(ddx, null, options));
		assertTrue(ex3.getMessage().contains("sourceDocuments"), ()->"'" + ex3.getMessage() + "' does not contain 'sourceDocuments'");
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

	
	private class MockPdfAssemblerService {
		private final AssemblerResult assemblerResult =  Mockito.mock(AssemblerResult.class);
		private final ArgumentCaptor<Document> ddx = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<AssemblerOptionsSpec> optionsArg = ArgumentCaptor.forClass(AssemblerOptionsSpec.class);
		@SuppressWarnings("unchecked")
		private final ArgumentCaptor<Map<String, Object>> sourceDocuments = ArgumentCaptor.forClass(Map.class);
		
		protected MockPdfAssemblerService() throws AssemblerServiceException, OperationException {
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

}
