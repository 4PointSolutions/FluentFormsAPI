package com._4point.aem.docservices.rest_services.client.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AdobeDocAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class RestServicesDocAssemblerServiceAdapter extends RestServicesServiceAdapter
		implements TraditionalDocAssemblerService {

	private static final String ASSEMBLE_DOCUMENT_PATH = "/services/AssemblerService/AssembleDocuments";
	private static final String DATA_PARAM_NAME = "ddx";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";
	
	protected static final String TEXT = "text/plain";

	// Only callable from Builder
	private RestServicesDocAssemblerServiceAdapter(WebTarget target) {
		super(target);
	}

	// Only callable from Builder
	private RestServicesDocAssemblerServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super(target, correlationId);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		WebTarget assembleDocTarget = baseTarget.path(ASSEMBLE_DOCUMENT_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {

			if (ddx != null) {
				multipart.field(DATA_PARAM_NAME, ddx.getInputStream(), MediaType.APPLICATION_XML_TYPE);
			} else {
				throw new NullPointerException("ddx can not be null");
			}
			
			if (inputs != null) { 			
				for (Entry<String, Object> param: inputs.entrySet()) {	
					      multipart.field(SOURCE_DOCUMENT_KEY, param.getKey());
				    	  multipart.field(SOURCE_DOCUMENT_VALUE, ((Document)param.getValue()).getInputStream(),
				    			  APPLICATION_PDF);		     
				 }			  
			 } else { 
				 throw new NullPointerException("inputs can not be null"); 
			 }

			if (adobAssemblerOptionSpec != null) {
				Boolean isFailOnError = adobAssemblerOptionSpec.isFailOnError();
				System.out.println("isFailOnError: "+isFailOnError);
				MultipartTransformer.create(multipart).transform(
						(t) -> isFailOnError == null ? t : t.field(IS_FAIL_ON_ERROR, isFailOnError.toString()));
			}

				Response result = postToServer(assembleDocTarget, multipart,  MediaType.APPLICATION_XML_TYPE);
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String msg = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='"
						+ resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					msg += "\n" + inputStreamtoString(entityStream);
				}
				throw new AssemblerServiceException(msg);
			}

			if (!result.hasEntity()) {
				throw new AssemblerServiceException(
						"Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			System.out.println("responseContentType " + responseContentType);
			if (responseContentType == null) {
				String msg = "Response from AEM server was null  "
						+ (responseContentType != null ? "content-type='" + responseContentType + "'"
								: "content-type was null")
						+ ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new AssemblerServiceException(msg);
			}
			String resultXml = result.readEntity(String.class);
			System.out.println("resultXml: "+resultXml);
		    Map<String, Document> resultMap = convertXmlDocument(resultXml);
			AssemblerResult assemblerResult = new AdobeDocAssemblerServiceAdapter(resultMap);
			return assemblerResult;

		} catch (IOException e) {
			throw new AssemblerServiceException(
					"I/O Error while  merging document. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new AssemblerServiceException("Error while posting to server", e);
		}

	}

	private Map<String, Document> convertXmlDocument(String assemblerResultXml) throws AssemblerServiceException {
		Map<String, Document> resultMap = new HashMap<String, Document>();
		DocumentBuilder db;
		byte[] bytesPdf = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(assemblerResultXml));
			org.w3c.dom.Document doc = db.parse(is);			
			NodeList nList = doc.getElementsByTagName("resultDocument");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					bytesPdf = Base64.getDecoder()
							.decode(eElement.getElementsByTagName("mergedDoc").item(0).getTextContent());
					Document concatenatedDoc = SimpleDocumentFactoryImpl.getFactory().create(bytesPdf);
					concatenatedDoc.setContentType(APPLICATION_PDF.toString());
					resultMap.put(eElement.getAttribute("documentName"), concatenatedDoc);
				}
			}
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new AssemblerServiceException("Error while parsing  to xml", e);
		}

		return resultMap;

	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) {

		return null;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) {

		return null;
	}

	public static AssemblerServiceBuilder builder() {
		return new AssemblerServiceBuilder();
	}

	public static class AssemblerServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();

		@Override
		public AssemblerServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public AssemblerServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public AssemblerServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public AssemblerServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
			return this;
		}

		@Override
		public AssemblerServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public AssemblerServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}

		public RestServicesDocAssemblerServiceAdapter build() {
			return new RestServicesDocAssemblerServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}
	}

}
