package com._4point.aem.docservices.rest_services.client.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
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
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerResultImpl;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

public class RestServicesDocAssemblerServiceAdapter extends RestServicesServiceAdapter
implements TraditionalDocAssemblerService {

	private static final String ASSEMBLE_DOCUMENT_PATH = "/services/AssemblerService/AssembleDocuments";
	private static final String DATA_PARAM_NAME = "ddx";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String IS_VALIDATE_ONLY = "isValidateOnly";
	private static final String IS_TAKE_OWNER_SHIP = "isTakeOwnerShip";
	private static final String JOB_LOG_LEVEL = "jobLogLevel";
	private static final String DEFAULT_STYLE = "defaultStyle";
	private static final String FIRST_BATES_NUMBER = "firstBatesNum";	
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";

	// Only callable from Builder
	private RestServicesDocAssemblerServiceAdapter(WebTarget target, Supplier<String> correlationId, AemServerType aemServerType) {
		super(target, correlationId, aemServerType);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		WebTarget assembleDocTarget = baseTarget.path(ASSEMBLE_DOCUMENT_PATH);

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, Objects.requireNonNull(ddx, "ddx can not be null").getInputStream(), MediaType.APPLICATION_XML_TYPE);					
			for (Entry<String, Object> param : Objects.requireNonNull(sourceDocuments, "source documents map can not be null").entrySet()) {
				multipart.field(SOURCE_DOCUMENT_KEY, param.getKey());
				multipart.field(SOURCE_DOCUMENT_VALUE, ((Document) param.getValue()).getInputStream(),
						APPLICATION_PDF);
			}

			if (assemblerOptionSpec != null) {
				Boolean isFailOnError = assemblerOptionSpec.isFailOnError();
				Boolean isTakeOwnerShip = assemblerOptionSpec.isTakeOwnership();
				Boolean isValidateOnly = assemblerOptionSpec.isValidateOnly();
				LogLevel jobLogLevel =  assemblerOptionSpec.getLogLevel();
				int firstBatesNum = assemblerOptionSpec.getFirstBatesNumber();
				String defaultStyle = assemblerOptionSpec.getDefaultStyle();;
				MultipartTransformer.create(multipart)
					.transform((t) -> isFailOnError == null ? t : t.field(IS_FAIL_ON_ERROR, isFailOnError.toString()))
					.transform((t) -> isValidateOnly == null ? t : t.field(IS_VALIDATE_ONLY, isValidateOnly.toString()))
					.transform((t) -> isTakeOwnerShip == null ? t : t.field(IS_TAKE_OWNER_SHIP, isTakeOwnerShip.toString()))
					.transform((t) -> jobLogLevel == null ? t : t.field(JOB_LOG_LEVEL, jobLogLevel.toString()))
					.transform((t) -> firstBatesNum == 0 ? t : t.field(FIRST_BATES_NUMBER, String.valueOf(firstBatesNum)))
					.transform((t) -> defaultStyle == null ? t : t.field(DEFAULT_STYLE, defaultStyle.toString()));
			}

			Response result = postToServer(assembleDocTarget, multipart, MediaType.APPLICATION_XML_TYPE);
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
				throw new AssemblerServiceException("Call to server succeeded but server failed to return assemblerResult xml.  This should never happen.");
			}

			MediaType responseContentType = result.getMediaType();
			if (responseContentType == null || !responseContentType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
				String msg = "Response from AEM server was  " + (responseContentType != null ? "content-type='" + responseContentType.toString() + "'"
								: "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new AssemblerServiceException(msg);
			}
			AssemblerResult assemblerResult = convertXmlToAssemblerResult((InputStream)result.getEntity());
			return assemblerResult;

		} catch (IOException e) {
			throw new AssemblerServiceException(
					"I/O Error while  merging document. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new AssemblerServiceException("Error while posting to server", e);
		}

	}

	// Package visibility so that it can be unit tested.
	/* package */public static AssemblerResult convertXmlToAssemblerResult(InputStream assemblerResultXml) throws AssemblerServiceException {
		Map<String, Document> resultMap = new HashMap<String, Document>();
		Map<String,List<String>> multipleResultsBlocks = new HashMap<String, List<String>>();
		List<String> successfulBlockNames = new ArrayList<String>();
		List<String> successfulDocumentNames = new ArrayList<String>();
		List<String> failedBlockNames = new ArrayList<String>();
		DocumentBuilder db;
		byte[] bytesPdf = null;
		AssemblerResultImpl assemblerResult = new AssemblerResultImpl();
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = db.parse(assemblerResultXml);
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
			getNodeValueForAttribute(doc, "jobLog", "logValue", assemblerResult);
			getNodeValueForAttribute(doc, "latestBatesNumber", "value", assemblerResult);
			getNodeValueForAttribute(doc, "numRequestedBlocks", "value", assemblerResult);	
			getNodeValeuForList(doc, "successfulDocumentNames", "successfulDocumentName", successfulDocumentNames);
			getNodeValeuForList(doc, "successfulBlockNames", "successfulBlockName", successfulBlockNames);
			getNodeValeuForList(doc, "failedBlockNames", "failedBlockName", failedBlockNames);	

			NodeList multipleResultBlocksNodeLi = doc.getElementsByTagName("multipleResultBlocks");
			for (int i = 0; i < multipleResultBlocksNodeLi.getLength(); i++) {
				Node node = multipleResultBlocksNodeLi.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;		
					Element childElement = (Element) eElement.getChildNodes().item(0);
					List<String> documentNamesLi = new ArrayList<String>();
					for (int j= 0; j < childElement.getChildNodes().getLength(); j++) {
						documentNamesLi.add(eElement.getElementsByTagName("documentName").item(j).getTextContent());
					}
					multipleResultsBlocks.put(eElement.getAttribute("name"), documentNamesLi);
				}
			}
			assemblerResult.setDocuments(resultMap);
			assemblerResult.setSuccessfulDocumentNames(successfulDocumentNames);
			assemblerResult.setSuccessfulBlockNames(successfulBlockNames);
			assemblerResult.setFailedBlockNames(failedBlockNames);
			assemblerResult.setMultipleResultsBlocks(multipleResultsBlocks);
			assemblerResult.setThrowables(Collections.emptyMap());	// Not currently supported, so we return an empty map.

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new AssemblerServiceException("Error while parsing xml", e);
		}
		return assemblerResult;
	}

	private static void getNodeValeuForList(org.w3c.dom.Document doc, String parentNodeName, String childNodeName,
			List<String> stringLi) {
		NodeList nodeLi = doc.getElementsByTagName(parentNodeName);
		for (int i = 0; i < nodeLi.getLength(); i++) {
			Node node = nodeLi.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;	
				NodeList childNodLi = eElement.getChildNodes();
				for (int j = 0; j < childNodLi.getLength(); j++) {
					Node childNode = childNodLi.item(j);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element) node;	
						stringLi.add(childElement.getElementsByTagName(childNodeName).item(j).getTextContent());
					}
				}
			}
		}
	}


	private static void getNodeValueForAttribute(org.w3c.dom.Document doc, String parentNodeName, String attributeName, AssemblerResultImpl assemblerResult) {
		int result = 0;
		Element eElement = (Element)doc.getElementsByTagName(parentNodeName).item(0);
		if(parentNodeName.equals("jobLog")) {
			byte[] bytesJobLog = Base64.getDecoder().decode(eElement.getAttribute(attributeName));
			assemblerResult.setJobLog(SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog));
		} else {
			result = Integer.parseInt(eElement.getAttribute(attributeName));
			if(parentNodeName.equals("latestBatesNumber")) {
				assemblerResult.setLastBatesNumber(result);
			} else {
				assemblerResult.setNumRequestedBlocks(result);
			}
		}
	}

	/*
	 * @Override public PDFAValidationResult isPDFA(Document inDoc,
	 * PDFAValidationOptionSpec options) {
	 * 
	 * return null; }
	 * 
	 * @Override public PDFAConversionResult toPDFA(Document inDoc,
	 * PDFAConversionOptionSpec options) {
	 * 
	 * return null; }
	 */
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

		@Override
		public Builder aemServerType(AemServerType serverType) {
			return builder.aemServerType(serverType);
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesDocAssemblerServiceAdapter build() {
			return new RestServicesDocAssemblerServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn(), this.getAemServerType());
		}
	}

}
