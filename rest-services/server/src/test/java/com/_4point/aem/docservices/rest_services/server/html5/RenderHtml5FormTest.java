package com._4point.aem.docservices.rest_services.server.html5;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com._4point.aem.docservices.rest_services.server.TestUtils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

class RenderHtml5FormTest {
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String DATA_REF_PARAM = "dataRef";
	private static final String CONTENT_ROOT_PARAM = "contentRoot";
	private static final String SUBMIT_URL_PARAM = "submitUrl";
	
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(RenderHtml5Form.class);

	private final RenderHtml5Form underTest = new RenderHtml5Form();

	private enum FormType { BY_VALUE, BY_REFERENCE_PATH, BY_REFERENCE_CRXURL, BY_REFERENCE_NO_CR, BY_REFERENCE_WITH_CR  };
	private enum DataType { NO_DATA, BY_VALUE, BY_REFERENCE };
	private enum SubmitUrlType { NONE, SUBMIT_URL };
	
	private enum HappyPathScenario {
		FORM_REF_ONLY(FormType.BY_REFERENCE_PATH, DataType.NO_DATA, SubmitUrlType.NONE),
		FORM_REF_DATA_REF(FormType.BY_REFERENCE_PATH, DataType.BY_REFERENCE, SubmitUrlType.SUBMIT_URL),
		FORM_REF_DATA_VAL(FormType.BY_REFERENCE_PATH, DataType.BY_VALUE, SubmitUrlType.NONE),
		FORM_REF_ONLY_CRX(FormType.BY_REFERENCE_CRXURL, DataType.NO_DATA, SubmitUrlType.SUBMIT_URL),
		FORM_REF_DATA_REF_CRX(FormType.BY_REFERENCE_CRXURL, DataType.BY_REFERENCE, SubmitUrlType.NONE),
		FORM_REF_DATA_CRX(FormType.BY_REFERENCE_CRXURL, DataType.BY_VALUE, SubmitUrlType.SUBMIT_URL),
		FORM_REF_ONLY_NO_CR(FormType.BY_REFERENCE_NO_CR, DataType.NO_DATA, SubmitUrlType.NONE),
		FORM_REF_DATA_REF_NO_CR(FormType.BY_REFERENCE_NO_CR, DataType.BY_REFERENCE, SubmitUrlType.SUBMIT_URL),
		FORM_REF_DATA_NO_CR(FormType.BY_REFERENCE_NO_CR, DataType.BY_VALUE, SubmitUrlType.NONE),
		FORM_REF_ONLY_WITH_CR(FormType.BY_REFERENCE_WITH_CR, DataType.NO_DATA, SubmitUrlType.SUBMIT_URL),
		FORM_REF_DATA_REF_WITH_CR(FormType.BY_REFERENCE_WITH_CR, DataType.BY_REFERENCE, SubmitUrlType.NONE),
		FORM_REF_DATA_WITH_CR(FormType.BY_REFERENCE_WITH_CR, DataType.BY_VALUE, SubmitUrlType.SUBMIT_URL),
//		FORM_VAL_ONLY(FormType.BY_VALUE, DataType.NO_DATA),		// Not supported at this time
//		FORM_VAL_DATA_REF(FormType.BY_VALUE, DataType.BY_REFERENCE),		// Not supported at this time
//		FORM_VAL_DATA_VAL(FormType.BY_VALUE, DataType.BY_VALUE)		// Not supported at this time
		;
		
		private final FormType formType;
		private final DataType dataType;
		private final SubmitUrlType submitType;

		private HappyPathScenario(FormType formType, DataType dataType, SubmitUrlType submitType) {
			this.formType = formType;
			this.dataType = dataType;
			this.submitType = submitType;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testDoPost_HappyPath_FormRefAndDataDoc(HappyPathScenario scenario) throws Exception {
		Path sampleForm = TestUtils.SAMPLE_FORM;
		String templateRef = sampleForm.toString();
		byte[] templateData = IOUtils.toByteArray(Files.newInputStream(sampleForm)); 
		String CRX_EXPECTED_CONTENT_ROOT = "crx://content/dam/formsanddocuments/";
		String CRX_EXPECTED_FILENAME = "SampleForm.xdp";
		String CRX_STRING = CRX_EXPECTED_CONTENT_ROOT + CRX_EXPECTED_FILENAME;
		
		Path sampleData = TestUtils.SAMPLE_DATA;
		String dataRef = sampleData.toString();
		byte[] formData = IOUtils.toByteArray(Files.newInputStream(sampleData));

		String expectedSubmitUrl = "http://example.com/foo/bar"; 
				
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
			
			@Override
			public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
				return mockRequestDispatcher();
			}
			
			@Override
			public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
				return mockRequestDispatcher();
			}
			
			private RequestDispatcher mockRequestDispatcher() {
				return new RequestDispatcher() {
					
					@Override
					public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
						if (scenario.formType == FormType.BY_REFERENCE_PATH) {
							assertEquals(sampleForm.getFileName().toString(), request.getAttribute(TEMPLATE_PARAM));
							assertEquals(sampleForm.getParent().toString().replace('\\', '/'), request.getAttribute(CONTENT_ROOT_PARAM).toString().replace('\\', '/'));
						} else if (scenario.formType == FormType.BY_REFERENCE_CRXURL) {
							assertEquals(CRX_EXPECTED_FILENAME, request.getAttribute(TEMPLATE_PARAM));
							assertEquals(CRX_EXPECTED_CONTENT_ROOT, request.getAttribute(CONTENT_ROOT_PARAM));
						} else if (scenario.formType == FormType.BY_VALUE) {
							assertArrayEquals(templateData, (byte[])(request.getAttribute(TEMPLATE_PARAM)));
							assertNull(request.getAttribute(CONTENT_ROOT_PARAM));
						} else if (scenario.formType == FormType.BY_REFERENCE_NO_CR) {
							assertEquals(CRX_EXPECTED_FILENAME, request.getAttribute(TEMPLATE_PARAM));
							assertNull(request.getAttribute(CONTENT_ROOT_PARAM));
						} else if (scenario.formType == FormType.BY_REFERENCE_WITH_CR) {
							assertEquals(CRX_EXPECTED_FILENAME, request.getAttribute(TEMPLATE_PARAM));
							assertEquals(CRX_EXPECTED_CONTENT_ROOT, request.getAttribute(CONTENT_ROOT_PARAM));
						}
						if (scenario.dataType == DataType.BY_VALUE) {
							assertArrayEquals(formData, (byte[])(request.getAttribute(DATA_PARAM)));
						} else if (scenario.dataType == DataType.BY_REFERENCE) {
							assertEquals(sampleData.toUri().toString(), request.getAttribute(DATA_REF_PARAM));
						}
						if (scenario.submitType == SubmitUrlType.SUBMIT_URL) {
							assertEquals(expectedSubmitUrl, request.getAttribute(SUBMIT_URL_PARAM));
						} else if (scenario.submitType == SubmitUrlType.NONE) {
							assertNull(request.getAttribute(SUBMIT_URL_PARAM));
						}
						response.setContentType("text/html");
						response.getOutputStream().write(resultDataBytes);
						response.setContentLength(resultDataBytes.length);
					}
					
					@Override
					public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
						throw new UnsupportedOperationException("RequestDispatcher.forward is not supported.");
					}
				};
			}
		});
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		if (scenario.formType == FormType.BY_REFERENCE_PATH) {
			request.addRequestParameter(TEMPLATE_PARAM, templateRef.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_REFERENCE_CRXURL) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_STRING.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_VALUE) {
			request.addRequestParameter(TEMPLATE_PARAM, formData, APPLICATION_XDP, "SampleForm.xdp");
		} else if (scenario.formType == FormType.BY_REFERENCE_NO_CR) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_EXPECTED_FILENAME.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_REFERENCE_WITH_CR) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_EXPECTED_FILENAME.getBytes(StandardCharsets.UTF_8), "text/plain");
			request.addRequestParameter(CONTENT_ROOT_PARAM, CRX_EXPECTED_CONTENT_ROOT.getBytes(StandardCharsets.UTF_8), "text/plain");
		}
		if (scenario.dataType == DataType.BY_VALUE) {
			request.addRequestParameter(DATA_PARAM, formData, APPLICATION_XML, "SampleFormData.xml");
		} else if (scenario.dataType == DataType.BY_REFERENCE) {
			request.addRequestParameter(DATA_PARAM, dataRef.getBytes(StandardCharsets.UTF_8), "text/plain");
		}
		
		if (scenario.submitType == SubmitUrlType.SUBMIT_URL) {
			request.addRequestParameter(SUBMIT_URL_PARAM, expectedSubmitUrl.getBytes(StandardCharsets.UTF_8), "text/plain");
		}
		
		underTest.doPost(request, response);
		
		assertNull(response.getStatusMessage());
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(TEXT_HTML, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	}

	@Test
	void testDoPost_HappyPath_AllParameters() throws ServletException, IOException, NoSuchFieldException {
	}

	@Disabled
	void testDoPost_BadAccept() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData);
		request.addHeader("Accept", APPLICATION_PDF);

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase(TEXT_HTML));
		assertThat(statusMsg, containsStringIgnoringCase(APPLICATION_PDF));
	}
	
	@Test
	void testDoPost_MissingFormParameter() throws Exception {
		final String expectedMessage = "Missing form parameter";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains(TEMPLATE_PARAM), "Expected '" + statusMessage + "' to contain '" + TEMPLATE_PARAM + "'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'.")
				);
	}
	
	@Test
	void testDoPost_BadTemplateParameter_BadContentType() throws Exception {
		final String expectedMessage = "invalid content type";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		String badContentType = "application/json";
		request.addRequestParameter(TEMPLATE_PARAM, "template".getBytes(StandardCharsets.UTF_8), badContentType);
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), "text/plain");;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains("Template"), "Expected '" + statusMessage + "' to contain 'Template'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'."),
				()->assertTrue(statusMessage.contains(badContentType), "Expected '" + statusMessage + "' to contain '" + badContentType + "'.")
				);
	}
	
	@Test
	void testDoPost_BadTemplateParameter_NotAnXdp() throws Exception {
		final String expectedMessage = "must point to an XDP file";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		String badTemplateValue = "crx://content/dam/formsanddocuments/";
		request.addRequestParameter(TEMPLATE_PARAM, badTemplateValue.getBytes(StandardCharsets.UTF_8), "text/plain");
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), "text/plain");;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains("Template"), "Expected '" + statusMessage + "' to contain 'Template'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'."),
				()->assertTrue(statusMessage.contains(badTemplateValue), "Expected '" + statusMessage + "' to contain '" + badTemplateValue + "'.")
				);
	}
	
	// For the time being, we are not allowing the template to be passed by Value, so check that we're detecting this and 
	// generating a reasonable response.
	@Test
	void testDoPost_BadTemplateParameter_PassByValue() throws Exception {
		final String expectedMessage = "RenderHtml5Form only supports rendering templates by reference at this time";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		String badTemplateValue = "crx://content/dam/formsanddocuments/";
		request.addRequestParameter(TEMPLATE_PARAM, badTemplateValue.getBytes(StandardCharsets.UTF_8), APPLICATION_XDP, "SampleForm.xdp");
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), "text/plain");;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'.")
				);
	}
	
	@Test
	void testDoPost_BadDataParameter_BadContentType() throws Exception {
		final String expectedMessage = "invalid content type";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		String badContentType = "application/json";
		request.addRequestParameter(TEMPLATE_PARAM, "template".getBytes(StandardCharsets.UTF_8), "text/plain");
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), badContentType);;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains("Data"), "Expected '" + statusMessage + "' to contain 'Data'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'."),
				()->assertTrue(statusMessage.contains(badContentType), "Expected '" + statusMessage + "' to contain '" + badContentType + "'.")
				);
	}
	
	@Test
	void testDoPost_BadSubmitUrlParameter_MalformedUrl() throws Exception {
		final String expectedMessage = "Malformed URL";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		String badSubmitUrl = "/foo/bar";
		request.addRequestParameter(TEMPLATE_PARAM, "template".getBytes(StandardCharsets.UTF_8), "text/plain");
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), "text/plain");;
		request.addRequestParameter(SUBMIT_URL_PARAM, badSubmitUrl.getBytes(StandardCharsets.UTF_8), "text/plain");;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains(SUBMIT_URL_PARAM), "Expected '" + statusMessage + "' to contain '" + SUBMIT_URL_PARAM + "'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'."),
				()->assertTrue(statusMessage.contains(badSubmitUrl), "Expected '" + statusMessage + "' to contain '" + badSubmitUrl + "'.")
				);
	}
	
	@Test
	void testDoPost_InternalServletError() throws Exception {
		final String expectedMessage = "Bogus ServletException";
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
			
			@Override
			public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
				return mockRequestDispatcher(expectedMessage);
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
				return mockRequestDispatcher(expectedMessage);
			}

			private RequestDispatcher mockRequestDispatcher(final String expectedMessage) {
				return new RequestDispatcher() {
					
					@Override
					public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
						throw new ServletException(expectedMessage);
					}
					
					@Override
					public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
						throw new ServletException(expectedMessage);
					}
				};
			}
		});

		request.addRequestParameter(TEMPLATE_PARAM, "template".getBytes(StandardCharsets.UTF_8), "text/plain");
		request.addRequestParameter(DATA_PARAM, "Data".getBytes(StandardCharsets.UTF_8), "text/plain");;

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String statusMessage = response.getStatusMessage();
		assertNotNull(statusMessage, "Expected statusMessage to not be null");
		assertAll(
				()->assertTrue(statusMessage.contains("Error while redirecting to html5 profile"), "Expected '" + statusMessage + "' to contain 'Error while redirecting to html5 profile'."),
				()->assertTrue(statusMessage.contains(expectedMessage), "Expected '" + statusMessage + "' to contain '" + expectedMessage + "'.")
				);
	}
	
	
}
