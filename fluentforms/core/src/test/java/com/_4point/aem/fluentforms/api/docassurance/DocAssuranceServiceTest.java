package com._4point.aem.fluentforms.api.docassurance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
import com.adobe.fd.readerextensions.client.UsageRights;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class DocAssuranceServiceTest {

	@Mock
	private TraditionalDocAssuranceService docAssuranceService;

	private DocAssuranceService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = new DocAssuranceServiceImpl(docAssuranceService);
	}

	@Test
	@DisplayName("Test secureDocument RE direct call.")
	void testRESecureDocument() throws DocAssuranceServiceException, FileNotFoundException {
		MockSecureDocumentMethod svc = new MockSecureDocumentMethod();
		
		Document inDoc = Mockito.mock(Document.class);
		EncryptionOptions encryptionOptions = null;
		SignatureOptions signatureOptions = null;
		ReaderExtensionOptions readerExtensionOptions = Mockito.mock(ReaderExtensionOptions.class);
		UnlockOptions unlockOptions = null;
		Document pdfResult = underTest.secureDocument(inDoc, encryptionOptions, signatureOptions, readerExtensionOptions, unlockOptions);
		
		// Verify that all the results are correct.
		assertTrue(svc.getInDocArg() == inDoc, "Expected the Document passed to AEM to match the input Document.");
		assertTrue(svc.getEncryptionOptionsArg() == encryptionOptions, "Expected the encryptionOptions passed to AEM to match the encryptionOptions used.");
		assertTrue(svc.getSignatureOptionsArg() == signatureOptions, "Expected the signatureOptions passed to AEM to match the signatureOptions used.");
		assertTrue(svc.getReaderExtensionOptionsArg() == readerExtensionOptions, "Expected the readerExtensionOptions passed to AEM to match the readerExtensionOptions used.");
		assertTrue(svc.getUnlockOptionsArg() == unlockOptions, "Expected the unlockOptions passed to AEM to match the unlockOptions used.");
		assertTrue(pdfResult == svc.getResult(), "Expected the Document returned by AEM to match the Document result.");
	}

	@Test
	@DisplayName("Test secureDocument RE fluent call with only the credential alias.")
	void testSecureDocumentREFluentDefaults() throws DocAssuranceServiceException {
		MockSecureDocumentMethod svc = new MockSecureDocumentMethod();
		
		Document inDoc = Mockito.mock(Document.class);
		String credentialAlias = "recred";
		
		Document pdfResult = underTest.secureDocument()
								.readerExtensionsOptions(credentialAlias)
							.done()
							.executeOn(inDoc);
		
		assertTrue(svc.getInDocArg() == inDoc, "Expected the Document passed to AEM to match the input Document.");
		assertNull(svc.getEncryptionOptionsArg(), "Expected EncryptionOptions passed to AEM to be null.");
		assertNull(svc.getSignatureOptionsArg(), "Expected SignatureOptions passed to AEM to be null.");
		assertNotNull(svc.getReaderExtensionOptionsArg(), "Expected ReaderExtensionOptions passed to AEM to not be null.");
		assertNull(svc.getUnlockOptionsArg(), "Expected UnlockOptions passed to AEM to be null.");
		assertTrue(pdfResult == svc.getResult(), "Expected the Document returned by AEM to match the Document result.");
		
		ReaderExtensionOptions reOptions = svc.getReaderExtensionOptionsArg();
		assertEquals(credentialAlias, reOptions.getCredentialAlias(), "Expected reader extensions credential alias to be " + credentialAlias);
		
		ReaderExtensionsOptionSpec reOptionSpec = reOptions.getReOptions();
		assertNotNull(reOptionSpec, "Expected the ReaderExtensionsOptionSpec passed to AEM to not be null.");
		assertEquals(true, reOptionSpec.isModeFinal(), "Expected the mode to be set to final.");
		assertNull(reOptionSpec.getMessage(), "Expected the message that is displayed within Adobe Reader when the rights enabled PDF document is opened to be null.");
		
		UsageRights usageRights = reOptionSpec.getUsageRights();
		assertNotNull(usageRights, "Expected usage rights not to be null.");
		assertFalse(usageRights.isEnabledFormFillIn(), "Expected enabledFormFillIn to be false.");
		assertFalse(usageRights.isEnabledDigitalSignatures(), "Expected enabledDigitalSignatures to be false.");
		assertFalse(usageRights.isEnabledOnlineForms(), "Expected enabledOnlineForms to be false.");
		assertFalse(usageRights.isEnabledFormDataImportExport(), "Expected enabledFormDataImportExport to be false.");
		assertFalse(usageRights.isEnabledDynamicFormFields(), "Expected enabledDynamicFormFields to be false.");
		assertFalse(usageRights.isEnabledBarcodeDecoding(), "Expected enabledBarcodedDecoding to be false.");
		assertFalse(usageRights.isEnabledComments(), "Expected enabledComments to be false.");
		assertFalse(usageRights.isEnabledCommentsOnline(), "Expected enabledCommentsOnline to be false.");
		assertFalse(usageRights.isEnabledDynamicFormPages(), "Expected enabledDynamicFormPages to be false.");
		assertFalse(usageRights.isEnabledEmbeddedFiles(), "Expected enabledEmbeddedFiles to be false.");
		assertFalse(usageRights.isEnabledSubmitStandalone(), "Expected enabledSubmitStandalone to be false.");
	}

	@Test
	@DisplayName("Test secureDocument RE fluent call with no credential alias.")
	void testSecureDocumentREFluentNoCred() throws DocAssuranceServiceException {
		Document inDoc = Mockito.mock(Document.class);
		
		NullPointerException e = assertThrows(NullPointerException.class, ()->underTest.secureDocument().readerExtensionsOptions(null).done().executeOn(inDoc));
		assertTrue(e.getMessage().contains("Credential Alias provided in Reader Extension options cannot be null"), "credentialAlias cannot be null.");
	}

	@Test
	@DisplayName("Test secureDocument RE fluent call with no document.")
	void testSecureDocumentREFluentNoDoc() throws DocAssuranceServiceException {
		NullPointerException e = assertThrows(NullPointerException.class, ()->underTest.secureDocument().executeOn(null));
		assertTrue(e.getMessage().contains("input Document cannot be null"), "input Document cannot be null.");
	}

	@Test
	@DisplayName("Test secureDocument RE fluent call with usage rights all set to true.")
	void testSecureDocumentREFluentURAllTrue() throws DocAssuranceServiceException {
		MockSecureDocumentMethod svc = new MockSecureDocumentMethod();
		
		Document inDoc = Mockito.mock(Document.class);
		String credentialAlias = "recred";
		
		Document pdfResult = underTest.secureDocument()
								.readerExtensionsOptions(credentialAlias)
								.setReOptions()
									.setMessage("test")
									.setUsageRights()
										.setEnabledBarcodeDecoding(true)
										.setEnabledComments(true)
										.setEnabledCommentsOnline(true)
										.setEnabledDigitalSignatures(true)
										.setEnabledDynamicFormFields(true)
										.setEnabledDynamicFormPages(true)
										.setEnabledEmbeddedFiles(true)
										.setEnabledFormDataImportExport(true)
										.setEnabledFormFillIn(true)
										.setEnabledOnlineForms(true)
										.setEnabledSubmitStandalone(true)
									.done()
								.done()
							.done()
							.executeOn(inDoc);
		
		assertTrue(svc.getInDocArg() == inDoc, "Expected the Document passed to AEM to match the input Document.");
		assertNull(svc.getEncryptionOptionsArg(), "Expected EncryptionOptions passed to AEM to be null.");
		assertNull(svc.getSignatureOptionsArg(), "Expected SignatureOptions passed to AEM to be null.");
		assertNotNull(svc.getReaderExtensionOptionsArg(), "Expected ReaderExtensionOptions passed to AEM to not be null.");
		assertNull(svc.getUnlockOptionsArg(), "Expected UnlockOptions passed to AEM to be null.");
		assertTrue(pdfResult == svc.getResult(), "Expected the Document returned by AEM to match the Document result.");
		
		ReaderExtensionOptions reOptions = svc.getReaderExtensionOptionsArg();
		assertEquals(credentialAlias, reOptions.getCredentialAlias(), "Expected reader extensions credential alias to be " + credentialAlias);
		
		ReaderExtensionsOptionSpec reOptionSpec = reOptions.getReOptions();
		assertNotNull(reOptionSpec, "Expected the ReaderExtensionsOptionSpec passed to AEM to not be null.");
		assertEquals(true, reOptionSpec.isModeFinal(), "Expected the mode to be set to final.");
		assertEquals("test", reOptionSpec.getMessage(), "Expected the message that is displayed within Adobe Reader when the rights enabled PDF document is opened to be 'test'.");
		
		UsageRights usageRights = reOptionSpec.getUsageRights();
		assertNotNull(usageRights, "Expected usage rights not to be null.");
		assertTrue(usageRights.isEnabledFormFillIn(), "Expected enabledFormFillIn to be true.");
		assertTrue(usageRights.isEnabledDigitalSignatures(), "Expected enabledDigitalSignatures to be true.");
		assertTrue(usageRights.isEnabledOnlineForms(), "Expected enabledOnlineForms to be true.");
		assertTrue(usageRights.isEnabledFormDataImportExport(), "Expected enabledFormDataImportExport to be true.");
		assertTrue(usageRights.isEnabledDynamicFormFields(), "Expected enabledDynamicFormFields to be true.");
		assertTrue(usageRights.isEnabledBarcodeDecoding(), "Expected enabledBarcodedDecoding to be true.");
		assertTrue(usageRights.isEnabledComments(), "Expected enabledComments to be true.");
		assertTrue(usageRights.isEnabledCommentsOnline(), "Expected enabledCommentsOnline to be true.");
		assertTrue(usageRights.isEnabledDynamicFormPages(), "Expected enabledDynamicFormPages to be true.");
		assertTrue(usageRights.isEnabledEmbeddedFiles(), "Expected enabledEmbeddedFiles to be true.");
		assertTrue(usageRights.isEnabledSubmitStandalone(), "Expected enabledSubmitStandalone to be true.");
	}

	@Test
	@DisplayName("Test secureDocument RE fluent call with usage rights half/half.")
	void testSecureDocumentREFluentURHalfHalf() throws DocAssuranceServiceException {
		MockSecureDocumentMethod svc = new MockSecureDocumentMethod();
		
		Document inDoc = Mockito.mock(Document.class);
		String credentialAlias = "recred";
		
		Document pdfResult = underTest.secureDocument()
								.readerExtensionsOptions(credentialAlias)
								.setReOptions()
									.setMessage("test")
									.setUsageRights()
										.setEnabledBarcodeDecoding(false)
										.setEnabledComments(false)
										.setEnabledCommentsOnline(false)
										.setEnabledDigitalSignatures(false)
										.setEnabledDynamicFormFields(false)
										.setEnabledDynamicFormPages(true)
										.setEnabledEmbeddedFiles(true)
										.setEnabledFormDataImportExport(true)
										.setEnabledFormFillIn(true)
										.setEnabledOnlineForms(true)
										.setEnabledSubmitStandalone(true)
									.done()
								.done()
							.done()
							.executeOn(inDoc);
		
		assertTrue(svc.getInDocArg() == inDoc, "Expected the Document passed to AEM to match the input Document.");
		assertNull(svc.getEncryptionOptionsArg(), "Expected EncryptionOptions passed to AEM to be null.");
		assertNull(svc.getSignatureOptionsArg(), "Expected SignatureOptions passed to AEM to be null.");
		assertNotNull(svc.getReaderExtensionOptionsArg(), "Expected ReaderExtensionOptions passed to AEM to not be null.");
		assertNull(svc.getUnlockOptionsArg(), "Expected UnlockOptions passed to AEM to be null.");
		assertTrue(pdfResult == svc.getResult(), "Expected the Document returned by AEM to match the Document result.");
		
		ReaderExtensionOptions reOptions = svc.getReaderExtensionOptionsArg();
		assertEquals(credentialAlias, reOptions.getCredentialAlias(), "Expected reader extensions credential alias to be " + credentialAlias);
		
		ReaderExtensionsOptionSpec reOptionSpec = reOptions.getReOptions();
		assertNotNull(reOptionSpec, "Expected the ReaderExtensionsOptionSpec passed to AEM to not be null.");
		assertEquals(true, reOptionSpec.isModeFinal(), "Expected the mode to be set to final.");
		assertEquals("test", reOptionSpec.getMessage(), "Expected the message that is displayed within Adobe Reader when the rights enabled PDF document is opened to be 'test'.");
		
		UsageRights usageRights = reOptionSpec.getUsageRights();
		assertNotNull(usageRights, "Expected usage rights not to be null.");
		assertTrue(usageRights.isEnabledFormFillIn(), "Expected enabledFormFillIn to be true.");
		assertFalse(usageRights.isEnabledDigitalSignatures(), "Expected enabledDigitalSignatures to be false.");
		assertTrue(usageRights.isEnabledOnlineForms(), "Expected enabledOnlineForms to be true.");
		assertTrue(usageRights.isEnabledFormDataImportExport(), "Expected enabledFormDataImportExport to be true.");
		assertFalse(usageRights.isEnabledDynamicFormFields(), "Expected enabledDynamicFormFields to be false.");
		assertFalse(usageRights.isEnabledBarcodeDecoding(), "Expected enabledBarcodedDecoding to be false.");
		assertFalse(usageRights.isEnabledComments(), "Expected enabledComments to be false.");
		assertFalse(usageRights.isEnabledCommentsOnline(), "Expected enabledCommentsOnline to be false.");
		assertTrue(usageRights.isEnabledDynamicFormPages(), "Expected enabledDynamicFormPages to be true.");
		assertTrue(usageRights.isEnabledEmbeddedFiles(), "Expected enabledEmbeddedFiles to be true.");
		assertTrue(usageRights.isEnabledSubmitStandalone(), "Expected enabledSubmitStandalone to be true.");
	}

	private class MockSecureDocumentMethod {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<Document> inDocArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<EncryptionOptions> encryptionOptionsArg = ArgumentCaptor.forClass(EncryptionOptions.class);
		private final ArgumentCaptor<SignatureOptions> signatureOptionsArg = ArgumentCaptor.forClass(SignatureOptions.class);
		private final ArgumentCaptor<ReaderExtensionOptions> reOptionsArg = ArgumentCaptor.forClass(ReaderExtensionOptions.class);
		private final ArgumentCaptor<UnlockOptions> unlockOptionsArg = ArgumentCaptor.forClass(UnlockOptions.class);
		
		protected MockSecureDocumentMethod() throws DocAssuranceServiceException {
			super();
			Mockito.lenient().when(docAssuranceService.secureDocument(inDocArg.capture(), encryptionOptionsArg.capture(), signatureOptionsArg.capture(), reOptionsArg.capture(), unlockOptionsArg.capture())).thenReturn(result);
		}

		protected Document getResult() {
			return result;
		}

		protected Document getInDocArg() {
			return inDocArg.getValue();
		}

		protected EncryptionOptions getEncryptionOptionsArg() {
			return encryptionOptionsArg.getValue();
		}

		protected SignatureOptions getSignatureOptionsArg() {
			return signatureOptionsArg.getValue();
		}

		protected ReaderExtensionOptions getReaderExtensionOptionsArg() {
			return reOptionsArg.getValue();
		}

		protected UnlockOptions getUnlockOptionsArg() {
			return unlockOptionsArg.getValue();
		}
	}

}
