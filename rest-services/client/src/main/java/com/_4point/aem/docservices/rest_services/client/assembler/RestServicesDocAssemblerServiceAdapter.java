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
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument;
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument.XmlDocumentException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerResultImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAConversionResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.core.Response.StatusType;

public class RestServicesDocAssemblerServiceAdapter extends RestServicesServiceAdapter
implements TraditionalDocAssemblerService {

	private static final String ASSEMBLE_DOCUMENT_SERVICE_NAME = "AssemblerService";
	private static final String ASSEMBLE_DOCUMENT_METHOD_NAME = "AssembleDocuments";
	private static final String TO_PDFA_METHOD_NAME = "ToPdfA";
	
	// invoke parameters
	private static final String DATA_PARAM_NAME = "ddx";
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

	// Only callable from Builder
	private RestServicesDocAssemblerServiceAdapter(WebTarget target, Supplier<String> correlationId, AemServerType aemServerType) {
		super(target, correlationId, aemServerType);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		WebTarget assembleDocTarget = baseTarget.path(constructStandardPath(ASSEMBLE_DOCUMENT_SERVICE_NAME, ASSEMBLE_DOCUMENT_METHOD_NAME));

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
						concatenatedDoc.setContentType(APPLICATION_PDF.toString());
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
			throw new AssemblerServiceException("Error while parsing xml", e);
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
		public AssemblerServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesDocAssemblerServiceAdapter build() {
			return new RestServicesDocAssemblerServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn(), this.getAemServerType());
		}
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
			throws AssemblerServiceException {
		throw new UnsupportedOperationException("isPDFA has not been implemented yet.");
	}

	@Override
	public PDFAConversionResult toPDFA(Document inPdf, PDFAConversionOptionSpec options) throws AssemblerServiceException {
			WebTarget toPdfaTarget = baseTarget.path(constructStandardPath(ASSEMBLE_DOCUMENT_SERVICE_NAME, TO_PDFA_METHOD_NAME));

			try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
				
				multipart.field(INPUT_DOCUMENT_PARAM, Objects.requireNonNull(inPdf, "input PDF can not be null").getInputStream(), APPLICATION_PDF);
				
				if (options != null) {
					
					ColorSpace colorSpace = options.getColorSpace();
					Compliance compliance = options.getCompliance();
					LogLevel logLevel = options.getLogLevel();
					List<Document> metadataSchemaExtensions = options.getMetadataSchemaExtensions();
					OptionalContent optionalContent = options.getOptionalContent();
					ResultLevel resultLevel = options.getResultLevel();
					Signatures signatures = options.getSignatures();
					Boolean removeInvalidXMPProperties = options.isRemoveInvalidXMPProperties();
					Boolean retainPDFFormState = options.isRetainPDFFormState();
					Boolean verify = options.isVerify();

					MultipartTransformer.create(multipart)
						.transform((t) -> colorSpace == null ? t : t.field(COLOR_SPACE_PARAM, colorSpace.toString()))
						.transform((t) -> compliance == null ? t : t.field(COMPLIANCE_PARAM, compliance.toString()))
						.transform((t) -> logLevel == null ? t : t.field(LOG_LEVEL_PARAM, logLevel.toString()))
						.transform((t) -> optionalContent == null ? t : t.field(OPTIONAL_CONTENT_PARAM, optionalContent.toString()))
						.transform((t) -> resultLevel == null ? t : t.field(RESULT_LEVEL_PARAM, resultLevel.toString()))
						.transform((t) -> signatures == null ? t : t.field(SIGNATURES_PARAM, signatures.toString()))
						.transform((t) -> removeInvalidXMPProperties == null ? t : t.field(REMOVE_INVALID_XMP_PARAM, removeInvalidXMPProperties.toString()))
						.transform((t) -> retainPDFFormState == null ? t : t.field(RETAIN_PDF_FORM_STATE_PARAM, retainPDFFormState.toString()))
						.transform((t) -> verify == null ? t : t.field(VERIFY_PARAM, verify.toString()));
					
					if (metadataSchemaExtensions != null) {
						for(Document extensionsDoc : metadataSchemaExtensions) {
							multipart.field(METADATA_EXTENSION_PARAM, extensionsDoc.getInputStream(), MediaType.APPLICATION_XML_TYPE);
						}
					}
				}

				Response result = postToServer(toPdfaTarget, multipart, MediaType.APPLICATION_XML_TYPE);
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
				PDFAConversionResult conversionResult = convertResponseToPdfaConversionResult((InputStream)result.getEntity());
				return conversionResult;
			} catch (IOException e) {
				throw new AssemblerServiceException("I/O Error while converting document. (" + baseTarget.getUri().toString() + ").", e);
			} catch (RestServicesServiceException e) {
				throw new AssemblerServiceException("Error while posting to server", e);
			} catch (XmlDocumentException e) {
				throw new AssemblerServiceException("Error extracting data from response XML.", e);
			}
	}

	private static PDFAConversionResult convertResponseToPdfaConversionResult(InputStream entityIs) throws XmlDocumentException {
		return convertXmlToPdfaConversionResult(XmlDocument.create(entityIs));
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
