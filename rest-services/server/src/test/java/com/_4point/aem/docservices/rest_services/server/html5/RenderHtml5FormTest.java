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

	private enum FormType { BY_VALUE, BY_REFERENCE };
	private enum DataType { NO_DATA, BY_VALUE, BY_REFERENCE };
	
	private enum HappyPathScenario {
		FORM_REF_ONLY(FormType.BY_REFERENCE, DataType.NO_DATA),
//		FORM_VAL_ONLY(FormType.BY_VALUE, DataType.NO_DATA),		// Not supported at this time
		FORM_REF_DATA_REF(FormType.BY_REFERENCE, DataType.BY_REFERENCE),
		FORM_REF_DATA_VAL(FormType.BY_REFERENCE, DataType.BY_VALUE),
//		FORM_VAL_DATA_REF(FormType.BY_VALUE, DataType.BY_REFERENCE),		// Not supported at this time
//		FORM_VAL_DATA_VAL(FormType.BY_VALUE, DataType.BY_VALUE)		// Not supported at this time
		;
		
		private final FormType formType;
		private final DataType dataType;

		private HappyPathScenario(FormType formType, DataType dataType) {
			this.formType = formType;
			this.dataType = dataType;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testDoPost_HappyPath_FormRefAndDataDoc(HappyPathScenario scenario) throws Exception {
		Path sampleForm = TestUtils.SAMPLE_FORM;
		String templateRef = sampleForm.toString();
		byte[] templateData = IOUtils.toByteArray(Files.newInputStream(sampleForm)); 
		
		Path sampleData = TestUtils.SAMPLE_DATA;
		String dataRef = sampleData.toString();
		byte[] formData = IOUtils.toByteArray(Files.newInputStream(sampleData));

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
						if (scenario.formType == FormType.BY_REFERENCE) {
							assertEquals(sampleForm.getFileName().toString(), request.getAttribute(TEMPLATE_PARAM));
						} else if (scenario.formType == FormType.BY_VALUE) {
							assertArrayEquals(templateData, (byte[])(request.getAttribute(TEMPLATE_PARAM)));
						}
						assertEquals("/content/dam/formsanddocuments/" + sampleForm.getParent().toString().replace('\\', '/'), request.getAttribute("contentRoot").toString().replace('\\', '/'));
						if (scenario.dataType == DataType.BY_VALUE) {
							assertArrayEquals(formData, (byte[])(request.getAttribute(DATA_PARAM)));
						} else if (scenario.dataType == DataType.BY_REFERENCE) {
							assertEquals(sampleData.toUri().toString(), request.getAttribute(DATA_REF_PARAM));
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
		if (scenario.formType == FormType.BY_REFERENCE) {
			request.addRequestParameter(TEMPLATE_PARAM, templateRef.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_VALUE) {
			request.addRequestParameter(TEMPLATE_PARAM, formData, APPLICATION_XDP, "SampleForm.xdp");
		}
		if (scenario.dataType == DataType.BY_VALUE) {
			request.addRequestParameter(DATA_PARAM, formData, APPLICATION_XML, "SampleFormData.xml");
		} else if (scenario.dataType == DataType.BY_REFERENCE) {
			request.addRequestParameter(DATA_PARAM, dataRef.getBytes(StandardCharsets.UTF_8), "text/plain");
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
}
