package com._4point.aem.docservices.rest_services.server.af;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import com._4point.aem.docservices.rest_services.server.data.DataCache;

import io.wcm.testing.mock.aem.junit5.AemContext;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

class RenderAdaptiveFormTest {
//	private static final String AF_URL_SUFFIX = ".html?wcmmode=disabled";
	private static final String AF_URL_SUFFIX = ".html";
	private static final String AF_URL_PREFIX = "/content/forms/af/";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String DATA_REF_PARAM = "dataRef";
	private static final String DATA_KEY_PARAM = "dataKey";
	private static final String CONTENT_ROOT_PARAM = "contentRoot";
	
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(RenderAdaptiveForm.class);

	private final RenderAdaptiveForm underTest = new RenderAdaptiveForm();

	private enum FormType { BY_VALUE, BY_REFERENCE_PATH, BY_REFERENCE_CRXURL, BY_REFERENCE_NO_CONTENT_ROOT, BY_REFERENCE_WITH_CONTENT_ROOT  };
	private enum DataType { NO_DATA, BY_VALUE, BY_REFERENCE };
	private enum SubmitUrlType { NONE };
	
	private enum HappyPathScenario {
//		FORM_REF_ONLY(FormType.BY_REFERENCE_PATH, DataType.NO_DATA, SubmitUrlType.NONE),
//		FORM_REF_DATA_REF(FormType.BY_REFERENCE_PATH, DataType.BY_REFERENCE, SubmitUrlType.NONE),
		FORM_REF_DATA_VAL(FormType.BY_REFERENCE_PATH, DataType.BY_VALUE, SubmitUrlType.NONE),
//		FORM_REF_ONLY_CRX(FormType.BY_REFERENCE_CRXURL, DataType.NO_DATA, SubmitUrlType.NONE),
//		FORM_REF_DATA_REF_CRX(FormType.BY_REFERENCE_CRXURL, DataType.BY_REFERENCE, SubmitUrlType.NONE),
//		FORM_REF_DATA_CRX(FormType.BY_REFERENCE_CRXURL, DataType.BY_VALUE, SubmitUrlType.NONE),
//		FORM_REF_ONLY_NO_CR(FormType.BY_REFERENCE_NO_CONTENT_ROOT, DataType.NO_DATA, SubmitUrlType.NONE),
//		FORM_REF_DATA_REF_NO_CR(FormType.BY_REFERENCE_NO_CONTENT_ROOT, DataType.BY_REFERENCE, SubmitUrlType.NONE),
//		FORM_REF_DATA_NO_CR(FormType.BY_REFERENCE_NO_CONTENT_ROOT, DataType.BY_VALUE, SubmitUrlType.NONE),
//		FORM_REF_ONLY_WITH_CR(FormType.BY_REFERENCE_WITH_CONTENT_ROOT, DataType.NO_DATA, SubmitUrlType.NONE),
//		FORM_REF_DATA_REF_WITH_CR(FormType.BY_REFERENCE_WITH_CONTENT_ROOT, DataType.BY_REFERENCE, SubmitUrlType.NONE),
//		FORM_REF_DATA_WITH_CR(FormType.BY_REFERENCE_WITH_CONTENT_ROOT, DataType.BY_VALUE, SubmitUrlType.NONE),
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
	void testDoGet_HappyPath(HappyPathScenario scenario) throws Exception {
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
			String filename = null;;
			
			String extractFilenameFromPath(String path) {
				int expectedPrefixLen = AF_URL_PREFIX.length();
				int expectedSuffixLen = AF_URL_SUFFIX.length();
				return path.substring(expectedPrefixLen, path.length() - expectedSuffixLen);
			}

			@Override
			public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
				fail("Wrong version of getRequestDispatcher called.");
				return mockRequestDispatcher();
			}
			
			@Override
			public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
				this.filename = extractFilenameFromPath(path);
				assertEquals(AF_URL_PREFIX + this.filename + AF_URL_SUFFIX, path);
				return mockRequestDispatcher();
			}
			
			private RequestDispatcher mockRequestDispatcher() {
				return new RequestDispatcher() {
					
					@Override
					public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
						if (scenario.formType == FormType.BY_REFERENCE_PATH) {
							assertEquals(sampleForm, Paths.get(filename));
						} else if (scenario.formType == FormType.BY_REFERENCE_CRXURL) {
							assertEquals(CRX_EXPECTED_FILENAME, filename);
						} else if (scenario.formType == FormType.BY_VALUE) {
							assertArrayEquals(templateData, (byte[])(request.getAttribute(TEMPLATE_PARAM)));
						} else if (scenario.formType == FormType.BY_REFERENCE_NO_CONTENT_ROOT) {
							assertEquals(CRX_EXPECTED_FILENAME, filename);
						} else if (scenario.formType == FormType.BY_REFERENCE_WITH_CONTENT_ROOT) {
							assertEquals(CRX_EXPECTED_FILENAME, filename);
						}
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
		if (scenario.formType == FormType.BY_REFERENCE_PATH) {
			request.addRequestParameter(TEMPLATE_PARAM, templateRef);
		} else if (scenario.formType == FormType.BY_REFERENCE_CRXURL) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_STRING.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_VALUE) {
			request.addRequestParameter(TEMPLATE_PARAM, formData, APPLICATION_XDP, "SampleForm.xdp");
		} else if (scenario.formType == FormType.BY_REFERENCE_NO_CONTENT_ROOT) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_EXPECTED_FILENAME.getBytes(StandardCharsets.UTF_8), "text/plain");
		} else if (scenario.formType == FormType.BY_REFERENCE_WITH_CONTENT_ROOT) {
			request.addRequestParameter(TEMPLATE_PARAM, CRX_EXPECTED_FILENAME.getBytes(StandardCharsets.UTF_8), "text/plain");
			request.addRequestParameter(CONTENT_ROOT_PARAM, CRX_EXPECTED_CONTENT_ROOT.getBytes(StandardCharsets.UTF_8), "text/plain");
		}
		
		if (scenario.dataType == DataType.BY_VALUE) {
			String dataKey = DataCache.addDataToCache(formData, APPLICATION_XML);
			request.addRequestParameter(DATA_KEY_PARAM, dataKey);
		} else if (scenario.dataType == DataType.BY_REFERENCE) {
			request.addRequestParameter(DATA_PARAM, dataRef.getBytes(StandardCharsets.UTF_8), "text/plain");
		}
		
		underTest.doGet(request, response);
		
		assertNull(response.getStatusMessage());
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(TEXT_HTML, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());

	}

}
