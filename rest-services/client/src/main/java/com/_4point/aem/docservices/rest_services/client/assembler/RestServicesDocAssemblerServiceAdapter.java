package com._4point.aem.docservices.rest_services.client.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument;
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument.XmlDocumentException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerResultImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAConversionResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

public class RestServicesDocAssemblerServiceAdapter extends RestServicesServiceAdapter
implements TraditionalDocAssemblerService {

	private static final String ASSEMBLE_DOCUMENT_SERVICE_NAME = "AssemblerService";
	private static final String ASSEMBLE_DOCUMENT_METHOD_NAME = "AssembleDocuments";
	private static final String TO_PDFA_METHOD_NAME = "ToPdfA";
	
	// invoke parameters
	private static final String DDX_PARAM_NAME = "ddx";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String IS_VALIDATE_ONLY = "isValidateOnly";
	private static final String IS_TAKE_OWNER_SHIP = "isTakeOwnerShip";
	private static final String JOB_LOG_LEVEL = "jobLogLevel";
	private static final String DEFAULT_STYLE = "defaultStyle";
	private static final String FIRST_BATES_NUMBER = "firstBatesNum";	
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";
	
	// ToPdfA parameters
	private static final String INPUT_DOCUMENT_PARAM = "inDoc";
	private static final String COLOR_SPACE_PARAM = "colorSpace";
	private static final String COMPLIANCE_PARAM = "compliance";
	private static final String LOG_LEVEL_PARAM = "logLevel";
	private static final String METADATA_EXTENSION_PARAM = "metadataExtension";
	private static final String OPTIONAL_CONTENT_PARAM = "optionalContent";
	private static final String RESULT_LEVEL_PARAM = "resultLevel";
	private static final String SIGNATURES_PARAM = "signatures";
	private static final String REMOVE_INVALID_XMP_PARAM = "removeInvalidXmlProperties";
	private static final String RETAIN_PDF_FORM_STATE_PARAM = "retainPdfFormState";
	private static final String VERIFY_PARAM = "verify";

	private final RestClient invokeRestClient;
	private final RestClient toPdfARestClient;

	// Only callable from Builder
	private RestServicesDocAssemblerServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
		super(correlationIdFn);
		this.invokeRestClient = builder.createClient(ASSEMBLE_DOCUMENT_SERVICE_NAME, ASSEMBLE_DOCUMENT_METHOD_NAME);
		this.toPdfARestClient = builder.createClient(ASSEMBLE_DOCUMENT_SERVICE_NAME, TO_PDFA_METHOD_NAME);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		Objects.requireNonNull(ddx, "ddx can not be null");
		Objects.requireNonNull(sourceDocuments, "source documents map can not be null");
		List<Map.Entry<String, Object>> sourceDocs = sourceDocuments.entrySet().stream().toList();

		try (MultipartPayload payload = invokeRestClient.multipartPayloadBuilder()
														.add(DDX_PARAM_NAME, ddx, ContentType.APPLICATION_XML)
														.addStrings(SOURCE_DOCUMENT_KEY, sourceDocs.stream().map(Map.Entry::getKey))
														.addDocs(SOURCE_DOCUMENT_VALUE, sourceDocs.stream().map(e -> (Document) e.getValue()))
														.transformAndAddStringVersion(IS_FAIL_ON_ERROR, assemblerOptionSpec, AssemblerOptionsSpec::isFailOnError)
														.transformAndAddStringVersion(IS_VALIDATE_ONLY, assemblerOptionSpec, AssemblerOptionsSpec::isValidateOnly)
														.transformAndAddStringVersion(IS_TAKE_OWNER_SHIP, assemblerOptionSpec, AssemblerOptionsSpec::isTakeOwnership)
														.transformAndAddStringVersion(JOB_LOG_LEVEL, assemblerOptionSpec, AssemblerOptionsSpec::getLogLevel)
														.transformAndAddStringVersion(FIRST_BATES_NUMBER, assemblerOptionSpec, AssemblerOptionsSpec::getFirstBatesNumber)
														.transformAndAdd(DEFAULT_STYLE, assemblerOptionSpec, AssemblerOptionsSpec::getDefaultStyle)
				 										.build()) {
			return payload.postToServer(ContentType.APPLICATION_XML)
						  .map(RestServicesDocAssemblerServiceAdapter::convertResponseToAssemblerResult)
						  .orElseThrow();
		} catch (XmlParsingException e) {
			throw new AssemblerServiceException("Error while parsing xml response. (" + invokeRestClient.target() + ").", e);
		} catch (IOException e) {
			throw new AssemblerServiceException("I/O Error while securing document. (" + invokeRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new AssemblerServiceException("Error while POSTing to server (" + invokeRestClient.target() + ").", e);
		}
	}

	@SuppressWarnings("serial")
	private static class XmlParsingException extends RuntimeException {
		public XmlParsingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private static AssemblerResult convertResponseToAssemblerResult(Response response) throws XmlParsingException  {
		return convertXmlToAssemblerResult(response.data());
	}
	
	// Package visibility so that it can be unit tested.
	/* package */ static AssemblerResult convertXmlToAssemblerResult(InputStream assemblerResultXml) throws XmlParsingException {
		Map<String, Document> resultMap = new HashMap<String, Document>();
		Map<String,List<String>> multipleResultsBlocks = new HashMap<String, List<String>>();
		DocumentBuilder db;
		byte[] bytesPdf = null;
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
					String contentType = eElement.getAttribute("contentType");
					if (contentType.isEmpty()) {
						// Defaulting to APPLICATION_PDF is not the best choice, however
						// it is required for backwards compatibility (since that was the initial implementation).
						concatenatedDoc.setContentType(ContentType.APPLICATION_PDF.contentType());
					} else {
						concatenatedDoc.setContentType(contentType);
					}
					resultMap.put(eElement.getAttribute("documentName"), concatenatedDoc);
				}
			}

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

		    return new AssemblerResultImpl(resultMap, // sourceDocuments
							    		   getNodeValuesAsList(doc, "failedBlockNames", "failedBlockName"), // failedBlockNames
							    		   getJobLog(doc), // jobLog
							    		   getLatestBatesNumber(doc), 	// lastBatesNumber
							    		   multipleResultsBlocks, // multipleResultsBlocks
							    		   getNumRequestedBlocks(doc), 	// numRequestedBlocks
							    		   getNodeValuesAsList(doc, "successfulBlockNames", "successfulBlockName"), // successfulBlockNames
							    		   getNodeValuesAsList(doc, "successfulDocumentNames", "successfulDocumentName"), // successfulDocumentNames
							    		   Collections.emptyMap());// throwables - Not currently supported, so we return an empty map.
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new XmlParsingException("Error while parsing xml", e);
		}
	}

	private static List<String> getNodeValuesAsList(org.w3c.dom.Document doc, String parentNodeName, String childNodeName) {
		ArrayList<String> stringList = new ArrayList<String>();
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
						stringList.add(childElement.getElementsByTagName(childNodeName).item(j).getTextContent());
					}
				}
			}
		}
		return stringList;
	}

	private static String getNodeValueForAttribute(org.w3c.dom.Document doc, String parentNodeName, String attributeName) {
		return ((Element)doc.getElementsByTagName(parentNodeName).item(0)).getAttribute(attributeName);
	}

	private static Document getJobLog(org.w3c.dom.Document doc) {
		byte[] bytesJobLog = Base64.getDecoder().decode(getNodeValueForAttribute(doc, "jobLog", "logValue"));
		return SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog);
	}

	private static int getLatestBatesNumber(org.w3c.dom.Document doc) {
		return Integer.parseInt(getNodeValueForAttribute(doc, "latestBatesNumber", "value"));
	}
	
	private static int getNumRequestedBlocks(org.w3c.dom.Document doc) {
		return Integer.parseInt(getNodeValueForAttribute(doc, "numRequestedBlocks", "value"));
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
	public static AssemblerServiceBuilder builder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return new AssemblerServiceBuilder(clientFactory);
	}

	public static class AssemblerServiceBuilder implements Builder {
		private final BuilderImpl builder;

		public AssemblerServiceBuilder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

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
		public AssemblerServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesDocAssemblerServiceAdapter build() {
			return new RestServicesDocAssemblerServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
			throws AssemblerServiceException {
		throw new UnsupportedOperationException("isPDFA has not been implemented yet.");
	}

	@Override
	public PDFAConversionResult toPDFA(Document inPdf, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		Objects.requireNonNull(inPdf, "input PDF can not be null");

		try (MultipartPayload payload = toPdfARestClient.multipartPayloadBuilder()
														.add(INPUT_DOCUMENT_PARAM, inPdf, ContentType.APPLICATION_DPL)
														.transformAndAddStringVersion(COLOR_SPACE_PARAM, options, PDFAConversionOptionSpec::getColorSpace)
														.transformAndAddStringVersion(COMPLIANCE_PARAM, options, PDFAConversionOptionSpec::getCompliance)
														.transformAndAddStringVersion(LOG_LEVEL_PARAM, options, PDFAConversionOptionSpec::getLogLevel)
														.transformAndAddStringVersion(OPTIONAL_CONTENT_PARAM, options, PDFAConversionOptionSpec::getOptionalContent)
														.transformAndAddStringVersion(RESULT_LEVEL_PARAM, options, PDFAConversionOptionSpec::getResultLevel)
														.transformAndAddStringVersion(SIGNATURES_PARAM, options, PDFAConversionOptionSpec::getSignatures)
														.transformAndAddStringVersion(REMOVE_INVALID_XMP_PARAM, options, PDFAConversionOptionSpec::isRemoveInvalidXMPProperties)
														.transformAndAddStringVersion(RETAIN_PDF_FORM_STATE_PARAM, options, PDFAConversionOptionSpec::isRetainPDFFormState)
														.transformAndAddStringVersion(VERIFY_PARAM, options, PDFAConversionOptionSpec::isVerify)
														.transformAndAddDocs(METADATA_EXTENSION_PARAM, options, PDFAConversionOptionSpec::getMetadataSchemaExtensions)
				 										.build()) {
			return payload.postToServer(ContentType.APPLICATION_XML)
						  .map(RestServicesDocAssemblerServiceAdapter::convertResponseToPdfaConversionResult)
						  .orElseThrow();
		} catch (XmlParsingException e) {
			throw new AssemblerServiceException("Error while parsing xml response. (" + invokeRestClient.target() + ").", e);
		} catch (IOException e) {
			throw new AssemblerServiceException("I/O Error while securing document. (" + invokeRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new AssemblerServiceException("Error while POSTing to server (" + invokeRestClient.target() + ").", e);
		}
	}
	
	private static PDFAConversionResult convertResponseToPdfaConversionResult(Response response) throws XmlParsingException {
		try {
			return convertXmlToPdfaConversionResult(XmlDocument.create(response.data()));
		} catch (XmlDocumentException e) {
			throw new XmlParsingException("Error while parsing xml", e);
		}
	}

	private static final String TO_PDFA_RESULT_BASE_XPATH = "/ToPdfAResult";
	private static final String CONVERSION_LOG_XPATH = TO_PDFA_RESULT_BASE_XPATH + "/ConversionLog";
	private static final String JOB_LOG_XPATH = TO_PDFA_RESULT_BASE_XPATH + "/JobLog";
	private static final String PDFA_DOCUMENT_XPATH = TO_PDFA_RESULT_BASE_XPATH + "/PdfADocument";
	private static final String IS_PDFA_XPATH = TO_PDFA_RESULT_BASE_XPATH + "/IsPdfA";
	
	private static PDFAConversionResult convertXmlToPdfaConversionResult(XmlDocument xmlDoc) throws XmlDocumentException {
		return new PDFAConversionResultImpl(xmlDoc.getDocument(CONVERSION_LOG_XPATH), 
											xmlDoc.getDocument(JOB_LOG_XPATH), 
											xmlDoc.getDocument(PDFA_DOCUMENT_XPATH), 
											xmlDoc.getBoolean(IS_PDFA_XPATH)
											);
	}
}
