package com._4point.aem.fluentforms.sampleapp.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.docservices.rest_services.client.helpers.AemDataFormat;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.GeneratePdfOutputArgumentBuilder;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.sampleapp.domain.DataService;
import com._4point.aem.fluentforms.sampleapp.domain.DataService.DataServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path(FluentFormsResources.RESOURCE_PATH)
public class FluentFormsResources {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsResources.class);

	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);

	protected static final String RESOURCE_PATH = "/FluentForms";

	// TODO:  Move this to configuration value
	String contentRoot = "crx:/content/dam/formsanddocuments";
	
	@Autowired
	DocumentFactory documentFactory;
	
	@Autowired
	OutputService outputService;
	
	@Path("/OutputServiceGeneratePdf")
	@GET
	@Produces({APPLICATION_PDF, "*/*;qs=0.8"})	// Will be selected if user requests PDF or nothing at all.
	public Response outputServiceGeneratePdf(@QueryParam("form") String templateName, @QueryParam("dataKey") String key) throws OutputServiceException, IOException {
		log.atInfo().addArgument(templateName).addArgument(key).log("Entered outputServiceGeneratePdf with template='{}', dataKey='{}'");
		if (templateName == null) {
			log.atError().log("'form' parameter not found in incoming request.");
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (outputService == null) {
			log.atError().log("OutputService was not available.");
			return Response.serverError().build();
		}
		if (key != null && !dataService.exists(key)) {
			log.atError().addArgument(key).log("Bad dataKey ({}).  Data was not available.");
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Optional<Document> data = retreiveData(key);
		var template = java.nio.file.Path.of(templateName);
		GeneratePdfOutputArgumentBuilder generatePDFOutput = applyContentRoot(outputService.generatePDFOutput());

		Document result = data.isPresent() ? generatePDFOutput.executeOn(template, data.orElseThrow())
				 						   : generatePDFOutput.executeOn(template);

		log.atInfo().log("Exiting outputServiceGeneratePdf");
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}
	private GeneratePdfOutputArgumentBuilder applyContentRoot(GeneratePdfOutputArgumentBuilder in) {
		log.atDebug().addArgument(contentRoot).log("Content Root='{}'.");
		return contentRoot != null && !contentRoot.isBlank() ? in.setContentRoot(PathOrUrl.from(contentRoot))
															 : in;
	}
	
	@Path("/OutputServiceGeneratePdf")
	@POST
	@Produces({APPLICATION_PDF, "*/*;qs=0.8"})	// Will be selected if user requests PDF or nothing at all.
	public Response outputServiceGeneratePdf_Post(@QueryParam("form") String templateName, InputStream dataInputStream) throws OutputServiceException, IOException {
		log.atInfo().addArgument(templateName).log("Entered outputServiceGeneratePdf_Post with template='{}' amd POSTed data");
		if (templateName == null) {
			log.atError().log("'form' parameter not found in incoming request.");
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (outputService == null) {
			log.atError().log("OutputService was not available.");
			return Response.serverError().build();
		}

		//  Read in data from data InputStream
		byte[] dataBytes = dataInputStream.readAllBytes();
		Optional<AemDataFormat> format = AemDataFormat.sniff(dataBytes);
		if (format.isEmpty()) { 
			log.atError().log("Unrecognized data format.");
			return Response.status(Status.BAD_REQUEST).build();
		}
		//  Sniff it and convert it to XML if it is JSON
		byte[] data = switch(format.orElseThrow()) {
			case JSON->convertJsonToXml(dataBytes);
			case XML->dataBytes;
			};
			
		log.atTrace().addArgument(()->new String(data, StandardCharsets.UTF_8)).log("XML data is '{}'.");
		
		//  Carry on as before
		var template = java.nio.file.Path.of(templateName);
		var generatePDFOutput = applyContentRoot(outputService.generatePDFOutput());

		Document result = generatePDFOutput.executeOn(template, data);

		log.atInfo().log("Exiting outputServiceGeneratePdf_Post");
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}

	private byte[] convertJsonToXml(byte[] jsonBytes) {
		log.atTrace().addArgument(()->new String(jsonBytes, StandardCharsets.UTF_8)).log("Incoming Json is '{}'.");
		JsonNode json = parseJson(jsonBytes).at("/afData/afBoundData");
		String rootElement = determineRootName(json);
		return convertToXml(json.at("/" + rootElement), rootElement);
	}

	private String determineRootName(JsonNode json) {
		List<String> topLevelFieldNames = iteratorToStream(json::fieldNames).toList();
		if (topLevelFieldNames.size() != 1) {
			// Should only be one root object.
			throw new IllegalStateException("Expected just one json root but found nodes %s".formatted(topLevelFieldNames.toString()));
		}
		return topLevelFieldNames.get(0);
	}

	private <T> Stream<T> iteratorToStream(Supplier<Iterator<T>> iterator) {
		return StreamSupport.stream(((Iterable<T>)()->(iterator.get())).spliterator(), false);
	}

	private static JsonNode parseJson(byte[] data) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode invoiceData = mapper.readValue(data, JsonNode.class);
			return invoiceData;
		} catch (IOException e) {
			throw new IllegalStateException("Error parsing Json.", e);
		}
	}
	
	private byte[] convertToXml(JsonNode json, String xmlRootElement) {
		try {
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
			ObjectWriter ow = xmlMapper.writer().withRootName(xmlRootElement);	// Set the root element name
			return ow.writeValueAsBytes(json);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Error converting JSON to XML.", e);
		}
	}


	@Autowired
	AdaptiveFormsService adaptiveFormsService;
	
	@Path("/AdaptiveFormsServiceRenderAdaptiveForm")
	@GET
	@Produces({MediaType.TEXT_HTML, "*/*;qs=0.8"})	// Will be selected if user requests HTML or nothing at all.
	public Response adaptiveFormsServiceRenderAdaptiveForm(@QueryParam("form") String templateName, @QueryParam("dataKey") String key) throws AdaptiveFormsServiceException, IOException {
		log.atInfo().addArgument(templateName).addArgument(key).log("Entered adaptiveFormsServiceRenderAdaptiveForm with template='{}', dataKey='{}'");
		if (templateName == null) return Response.status(Status.BAD_REQUEST).build();
		if (adaptiveFormsService == null) return Response.serverError().build();
		if (key != null && !dataService.exists(key)) Response.status(Status.BAD_REQUEST).build();
		
		Optional<Document> data = retreiveData(key);
		
		Document result = data.isPresent() ? adaptiveFormsService.renderAdaptiveForm(templateName, data.orElseThrow())
										   : adaptiveFormsService.renderAdaptiveForm(templateName);
		
		log.atInfo().log("Exiting adaptiveFormsServiceRenderAdaptiveForm");
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}

	private Optional<Document> retreiveData(String key) {
		Optional<Document> data = Optional.ofNullable(key)
				  						  .map(dataService::load)
				  						  .map(this::convertDataToDocument);
		return data;
	}
	
	private Document convertDataToDocument(byte[] data) {
		Document doc = documentFactory.create(data);
		return AemDataFormat.sniff(data)						// sniff the data
							.map(AemDataFormat::getContentType)	// if it's a recognized type
							.map(doc::setContentType)			// set the content type
							.orElse(doc);						// otherwise return the doc without a content type.
	}

	@Autowired
	DataService dataService;
	
	@Path("/SaveData")
	@POST
	@Produces({MediaType.TEXT_HTML, "*/*;qs=0.8"})	// Will be selected if user requests HTML or nothing at all.
	public Response saveData(@QueryParam("dataKey") String key, InputStream body) {
		log.atInfo().addArgument(key).log("Entered saveData with dataKey='{}'");
		try {
			byte[] jsonBytes = Objects.requireNonNull(body).readAllBytes();
			log.atTrace().addArgument(()->new String(jsonBytes, StandardCharsets.UTF_8)).log("Incoming Json is '{}'.");
			dataService.save(Objects.requireNonNull(key), jsonBytes);
			log.atInfo().log("Exiting saveData");
			return Response.noContent().build();
		} catch (DataServiceException e) {
			log.atError().setCause(e).log("Error saving data, returning Bad Request.");
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IOException e) {
			log.atError().setCause(e).log("Error saving data, returning Internal Server Error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Autowired
	Html5FormsService html5FormService;
	
	@Path("/Html5FormsServiceRenderHtml5Form")
	@GET
	@Produces({MediaType.TEXT_HTML, "*/*;qs=0.8"})	// Will be selected if user requests HTML or nothing at all.
	public Response htmlFormsServiceRenderHtml5Form(@QueryParam("form") String templateName, @QueryParam("dataKey") String key) throws Html5FormsServiceException, IOException {
		log.atInfo().addArgument(templateName).addArgument(key).log("Entered html5FormsServiceRenderHtml5Form with template='{}', dataKey='{}'");
		if (templateName == null) return Response.status(Status.BAD_REQUEST).build();
		if (adaptiveFormsService == null) return Response.serverError().build();
		if (key != null && !dataService.exists(key)) Response.status(Status.BAD_REQUEST).build();
		
		Optional<Document> data = retreiveData(key);
		
		Document result = data.isPresent() ? html5FormService.renderHtml5Form(templateName, data.orElseThrow())
										   : html5FormService.renderHtml5Form(templateName);
		
		log.atInfo().log("Exiting html5FormsServiceRenderHtml5Form");
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}

}
