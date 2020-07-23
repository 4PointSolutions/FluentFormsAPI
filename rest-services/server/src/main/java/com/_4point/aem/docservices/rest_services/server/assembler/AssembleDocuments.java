package com._4point.aem.docservices.rest_services.server.assembler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.FormParameters;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerArgumentBuilder;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=FormsService.AssembleDocuments Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths("/services/AssemblerService/AssembleDocuments")
public class AssembleDocuments extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(AssembleDocuments.class);
	private static final String DDX = "ddx";
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String IS_TAKE_OWNER_SHIP = "isTakeOwnerShip";
	private static final String DEFAULT_STYLE = "defaultStyle";
	private static final String FIRST_BATES_NUMBER = "firstBatesNumber";
	private static final String LOG_LEVEL = "logLevel";
	private static final String IS_VALIDATE_ONLY = "isValidatedOnly";

	@Reference
	private com.adobe.fd.assembler.service.AssemblerService adobeAssembleService;

	private final Supplier<TraditionalDocAssemblerService> assemblerServiceFactory = this::getAdobeAssemblerService;

	private final DocumentFactory docFactory = DocumentFactory.getDefault();

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) { // Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName(), e); // Make sure this gets into
			// our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		AssemblerService assemblerService = new AssemblerServiceImpl(assemblerServiceFactory.get(),
				UsageContext.SERVER_SIDE);
		try {
			Map<String, Object> sourceDocuments = new HashMap<String, Object>();
			RequestParameter ddxParam = FormParameters.getMandatoryParameter(request,DDX);
			RequestParameter[] soruceDocName =  FormParameters.getMandatoryParameters(request, SOURCE_DOCUMENT_KEY);
			RequestParameter[] sourceDocs = FormParameters.getMandatoryParameters(request, SOURCE_DOCUMENT_VALUE);
			if (soruceDocName.length == sourceDocs.length) {
				for (int i = 0; i < soruceDocName.length; i++) {
					log.info("Document Name: " + soruceDocName[i].toString());
					sourceDocuments.put(soruceDocName[i].getString(), docFactory.create(sourceDocs[i].get()));
				}
			}
			
			RequestParameter isFailonError = request.getRequestParameter(IS_FAIL_ON_ERROR);
			RequestParameter isTakeOwnerShip = request.getRequestParameter(IS_TAKE_OWNER_SHIP);
			RequestParameter isValidateOnly = request.getRequestParameter(IS_VALIDATE_ONLY);
			RequestParameter logLevel = request.getRequestParameter(LOG_LEVEL);
			RequestParameter firstBatesNumber = request.getRequestParameter(FIRST_BATES_NUMBER);
			RequestParameter defaultStyle = request.getRequestParameter(DEFAULT_STYLE);
           			
			Document ddx = docFactory.create(ddxParam.get());
			AssemblerArgumentBuilder argumentBuilder = assemblerService.invoke()
					.transform(b -> isFailonError == null ? b
							: b.setFailOnError(Boolean.valueOf(isFailonError.toString())))
					.transform(b -> isTakeOwnerShip == null ? b
							: b.setTakeOwnership(Boolean.valueOf(isTakeOwnerShip.toString())))
					.transform(b -> isValidateOnly == null ? b
							: b.setValidateOnly((Boolean.valueOf(isValidateOnly.toString()))))
					.transform(b -> logLevel == null ? b : b.setLogLevel(LogLevel.valueOf(logLevel.toString())))
					.transform(b -> firstBatesNumber == null ? b
							: b.setFirstBatesNumber(Integer.parseInt(firstBatesNumber.toString())))
					.transform(b -> defaultStyle == null ? b : b.setDefaultStyle(defaultStyle.toString()));

			try (AssemblerResult assemblerResult = argumentBuilder.executeOn(ddx, sourceDocuments)) {
				String assemblerResultxml = convertAssemblerResultToxml(assemblerResult);
				String contentType = ContentType.APPLICATION_XML.toString();	// We know the result is always PDF.
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				response.getWriter().write(assemblerResultxml);
			} catch (TransformerFactoryConfigurationError | ParserConfigurationException | TransformerException e) {
				throw new InternalServerErrorException(
						"Internal Error while Converting assembler result to xml. (" + e.getMessage() + ").", e);
			}
		} catch (AssemblerServiceException | IOException | OperationException ex1) {
			throw new InternalServerErrorException("Internal Error while merging PDF. (" + ex1.getMessage() + ").",
					ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while merging PDF", ex2);
		}

	}

	// Package visibility so that it can be unit tested.
	/* package */ static String convertAssemblerResultToxml(AssemblerResult assemblerResult)
			throws InternalServerErrorException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		log.info("Converting assemblerResult to xml");
		Map<String, Document> resultDocMap = assemblerResult.getDocuments();
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		org.w3c.dom.Document document = documentBuilder.newDocument();
		org.w3c.dom.Element root = document.createElement("assemblerResult");
		document.appendChild(root);
		if (MapUtils.isNotEmpty(resultDocMap)) {
			resultDocMap.forEach((docName, resultDoc) -> {
					addMapOfResultDocInXml(docName, resultDoc, document, root);
			});
		}

		createElementList(document, root, "failedBlockNames", "failedBlockName", assemblerResult.getFailedBlockNames());
		createElementList(document, root, "successfulDocumentNames", "successfulDocumentName", assemblerResult.getSuccessfulDocumentNames());
		createElementList(document, root, "successfulBlockNames", "successfulBlockName", assemblerResult.getSuccessfulBlockNames());
		createElementWithAttribute(document, root, "latestBatesNumber", "value", assemblerResult.getLastBatesNumber());
		createElementWithAttribute(document, root, "numRequestedBlocks", "value", assemblerResult.getNumRequestedBlocks());

		
		if (MapUtils.isNotEmpty(assemblerResult.getMultipleResultsBlocks())) {
			assemblerResult.getMultipleResultsBlocks().forEach((blockName,docNames)-> {
				Element resultBlockName = document.createElement("multipleResultBlocks");
				root.appendChild(resultBlockName);
				Attr nameAttr = document.createAttribute("name");
				nameAttr.setValue(blockName);
				resultBlockName.setAttributeNode(nameAttr);
				Element documentNames  = document.createElement("documentNames");			
				if(CollectionUtils.isNotEmpty(docNames)) {
					docNames.forEach(docName -> {
						Element documentName = document.createElement("documentName");	
						documentName.appendChild(document.createTextNode(docName));
						documentNames.appendChild(documentName);
					});
				}	
				resultBlockName.appendChild(documentNames);
			});
		}

		Element joblog = document.createElement("jobLog");
		root.appendChild(joblog);
		Document jobLogDoc = assemblerResult.getJobLog();
		if (jobLogDoc != null) {
			Attr logAttr = document.createAttribute("joblogValue");
			logAttr.setValue(toBase64String(jobLogDoc));
			joblog.setAttributeNode(logAttr);
		}

		return domToString(document);
	}

	private static String domToString(org.w3c.dom.Document document)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		DOMSource domSource = new DOMSource(document);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
	}

	private static String toBase64String(Document doc) throws InternalServerErrorException {
		try {
			return Base64.getEncoder().encodeToString(IOUtils.toByteArray(doc.getInputStream()));
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new InternalServerErrorException("Error while reading job log from assembler result. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
		}
	}

	private static void addMapOfResultDocInXml(String docName, Document resultDoc, org.w3c.dom.Document document, Element root) {
		try {
			Element result = document.createElement("resultDocument");
			root.appendChild(result);
			Attr attr = document.createAttribute("documentName");
			attr.setValue(docName);
			result.setAttributeNode(attr);
			if (resultDoc != null) {
				byte[] concatenatedPdf = null;
				concatenatedPdf = IOUtils.toByteArray(resultDoc.getInputStream());
				if (concatenatedPdf != null) {
					String doc = Base64.getEncoder().encodeToString(concatenatedPdf);
					Element mergedDoc = document.createElement("mergedDoc");
					mergedDoc.appendChild(document.createTextNode(doc));
					result.appendChild(mergedDoc);
				}
			}
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new IllegalStateException("Error while reading result document. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
		}
	}

	private static void createElementWithAttribute(org.w3c.dom.Document document, Element root, String parentElementName,
			String attributeName, int assemblerResultProperty) {
		Element latestBatesNum = document.createElement(parentElementName);
		root.appendChild(latestBatesNum);
		Attr latestBatesNumberAttr = document.createAttribute(attributeName);
		latestBatesNumberAttr.setValue(Integer.toString(assemblerResultProperty));
		latestBatesNum.setAttributeNode(latestBatesNumberAttr);

	}

	private static void createElementList(org.w3c.dom.Document document, org.w3c.dom.Element root,
			String parentElementName, String childlementName, List<String> stringList) {
		Element elementName = document.createElement(parentElementName);
		root.appendChild(elementName);
		if (CollectionUtils.isNotEmpty(stringList)) {
			stringList.forEach(assemblerResultPropertyName -> createElement(document, elementName, childlementName,
					assemblerResultPropertyName));
		}
	}

	private static void createElement(org.w3c.dom.Document document, Element Parent, String elementName,
			String elementValue) {
		Element assemblerResultProperty = document.createElement(elementName);
		assemblerResultProperty.appendChild(document.createTextNode(elementValue));
		Parent.appendChild(assemblerResultProperty);
	}

	private TraditionalDocAssemblerService getAdobeAssemblerService() {
		return new AdobeAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}

}
