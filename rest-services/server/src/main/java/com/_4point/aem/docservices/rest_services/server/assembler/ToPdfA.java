package com._4point.aem.docservices.rest_services.server.assembler;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.FormParameters;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=AssemblerService.ToPdfA Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/AssemblerService/ToPdfA")
public class ToPdfA  extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(ToPdfA.class);
	
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
	

	@Reference
	private com.adobe.fd.assembler.service.AssemblerService adobeAssembleService;

	private final Supplier<TraditionalDocAssemblerService> assemblerServiceFactory = this::getAdobeAssemblerService;

	private final DocumentFactory docFactory = DocumentFactory.getDefault();

	private TraditionalDocAssemblerService getAdobeAssemblerService() {
		return new AdobeAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}

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
		AssemblerService assemblerService = new AssemblerServiceImpl(assemblerServiceFactory.get(),	UsageContext.SERVER_SIDE);
		
		RequestParameter inputDocumentParam = FormParameters.getMandatoryParameter(request,INPUT_DOCUMENT_PARAM);
		RequestParameter colorSpaceParam = request.getRequestParameter(COLOR_SPACE_PARAM);
		RequestParameter complianceParam = request.getRequestParameter(COMPLIANCE_PARAM);
		RequestParameter logLevelParam = request.getRequestParameter(LOG_LEVEL_PARAM);
		RequestParameter[] metadataExtensions = request.getRequestParameters(METADATA_EXTENSION_PARAM);
		RequestParameter optionalContentParam = request.getRequestParameter(OPTIONAL_CONTENT_PARAM);
		RequestParameter resultLevelParam = request.getRequestParameter(RESULT_LEVEL_PARAM);
		RequestParameter signaturesParam = request.getRequestParameter(SIGNATURES_PARAM);
		RequestParameter removeInvalidXmpPropertiesParam = request.getRequestParameter(REMOVE_INVALID_XMP_PARAM);
		RequestParameter retainPdfFormStateParam = request.getRequestParameter(RETAIN_PDF_FORM_STATE_PARAM);
		RequestParameter isVerifyParam = request.getRequestParameter(VERIFY_PARAM);
		try {
			PDFAConversionResult result = assemblerService.toPDFA()
														  .transform(b -> colorSpaceParam == null ? b : b.setColorSpace(ColorSpace.valueOf(colorSpaceParam.toString())))
														  .transform(b -> complianceParam == null ? b : b.setCompliance(Compliance.valueOf(complianceParam.toString())))
														  .transform(b -> logLevelParam == null ? b : b.setLogLevel(LogLevel.valueOf(logLevelParam.toString())))
														  .transform(b -> metadataExtensions == null ? b : b.setMetadataSchemaExtensions(Arrays.stream(metadataExtensions)	// Convert array of RequestParameters
																																			   .map(RequestParameter::get)	// to a List<Document>
																																			   .map(docFactory::create)
																																			   .collect(Collectors.toList())))
														  .transform(b -> optionalContentParam == null ? b : b.setOptionalContent(OptionalContent.valueOf(optionalContentParam.toString())))
														  .transform(b -> resultLevelParam == null ? b : b.setResultLevel(ResultLevel.valueOf(resultLevelParam.toString())))
														  .transform(b -> signaturesParam == null ? b : b.setSignatures(Signatures.valueOf(signaturesParam.toString())))
														  .transform(b -> removeInvalidXmpPropertiesParam == null ? b : b.setRemoveInvalidXMPProperties(Boolean.valueOf(removeInvalidXmpPropertiesParam.toString())))
														  .transform(b -> retainPdfFormStateParam == null ? b : b.setRetainPDFFormState(Boolean.valueOf(retainPdfFormStateParam.toString())))
														  .transform(b -> isVerifyParam == null ? b : b.setVerify(Boolean.valueOf(isVerifyParam.toString())))
														  .executeOn(docFactory.create(inputDocumentParam.get()));
			
			response.setContentType(ContentType.APPLICATION_XML.toString());	// We know the result is always XML.
			writeResult(result, response.getWriter());
		} catch (AssemblerServiceException e) {
			throw new InternalServerErrorException("Internal Error while converting PDF to PDF/A. (" + e.getMessage() + ").", e);
		} catch (IOException| XMLStreamException | FactoryConfigurationError e) {
			throw new InternalServerErrorException("Internal Error while writing response. (" + e.getMessage() + ").", e);
		}
	}

	private static void writeResult(PDFAConversionResult result, Writer writer) throws IOException, XMLStreamException, FactoryConfigurationError {
		XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
		xmlWriter.writeStartDocument("UTF-8", "1.0");
		xmlWriter.writeStartElement("ToPdfAResult");
		
		xmlWriter.writeStartElement("ConversionLog");
		xmlWriter.writeCharacters(toBase64String(result.getConversionLog()));
		xmlWriter.writeEndElement();
		
		xmlWriter.writeStartElement("JobLog");
		xmlWriter.writeCharacters(toBase64String(result.getJobLog()));
		xmlWriter.writeEndElement();
		
		xmlWriter.writeStartElement("PdfADocument");
		xmlWriter.writeCharacters(toBase64String(result.getPDFADocument()));
		xmlWriter.writeEndElement();
		
		xmlWriter.writeStartElement("IsPdfA");
		xmlWriter.writeCharacters(result.isPDFA().toString());
		xmlWriter.writeEndElement();
		
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		xmlWriter.flush();
	}
	
	private static String toBase64String(Document doc) throws IOException {
		return Base64.getEncoder().encodeToString(IOUtils.toByteArray(doc.getInputStream()));
	}
}
