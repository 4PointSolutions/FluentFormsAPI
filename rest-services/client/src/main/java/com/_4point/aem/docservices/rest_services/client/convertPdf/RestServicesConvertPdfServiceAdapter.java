package com._4point.aem.docservices.rest_services.client.convertPdf;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;

public class RestServicesConvertPdfServiceAdapter extends RestServicesServiceAdapter implements TraditionalConvertPdfService {
	private static final Logger logger = LoggerFactory.getLogger(RestServicesConvertPdfServiceAdapter.class);
	
	private static final String CONVERT_PDF_SERVICE_NAME = "ConvertPdfService";
	private static final String TO_IMAGE_METHOD_NAME = "ToImage";
	private static final String TO_PS_METHOD_NAME = "ToPS";
	private static final String PDF_PARAM = "inPdfDoc";

	private static final String CMYK_POLICY_PARAM = "toImageOptionsSpec.cmykPolicy";
	private static final String COLOR_COMPRESSION_PARAM = "toImageOptionsSpec.colorCompression";
	private static final String COLOR_SPACE_PARAM = "toImageOptionsSpec.colorSpace";
	private static final String PNG_FILTER_PARAM = "toImageOptionsSpec.filter";
	private static final String JPEG_FORMAT_PARAM = "toImageOptionsSpec.format";
	private static final String GRAY_SCALE_COMPRESSION_PARAM = "toImageOptionsSpec.grayScaleCompression";
	private static final String GRAY_SCALE_POLICY_PARAM = "toImageOptionsSpec.grayScalePolicy";
	private static final String IMAGE_CONVERT_FORMAT_PARAM = "toImageOptionsSpec.imageConvertFormat";
	private static final String IMAGE_SIZE_HEIGHT_PARAM = "toImageOptionsSpec.imageSizeHeight";
	private static final String IMAGE_SIZE_WIDTH_PARAM = "toImageOptionsSpec.imageSizeWidth";
	private static final String IMAGE_INCLUDE_COMMENTS_PARAM = "toImageOptionsSpec.includeComments";
	private static final String INTERLACE_PARAM = "toImageOptionsSpec.interlace";
	private static final String MONOCHROME_COMPRESSION_PARAM = "toImageOptionsSpec.monochrome";
	private static final String MULTI_PAGE_TIFF_PARAM = "toImageOptionsSpec.multiPageTiff";
	private static final String IMAGE_PAGE_RANGE_PARAM = "toImageOptionsSpec.pageRange";
	private static final String RESOLUTION_PARAM = "toImageOptionsSpec.resolution";
	private static final String RGB_POLICY_PARAM = "toImageOptionsSpec.rgbPolicy";
	private static final String ROWS_PER_STRIP_PARAM = "toImageOptionsSpec.rowsPerStrip";
	private static final String TILE_SIZE_PARAM = "toImageOptionsSpec.tileSize";
	private static final String USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM = "toImageOptionsSpec.useLegacyImageSizeBehavior";
	private static final String COLOR_PARAM = "toPSOptionsSpec.color";
	private static final String FONT_INCLUSION_PARAM = "toPSOptionsSpec.fontInclusion";
	private static final String LINE_WEIGHT_PARAM = "toPSOptionsSpec.lineWeight";
	private static final String PS_PAGE_RANGE_PARAM = "toPSOptionsSpec.pageRange";
	private static final String PAGE_SIZE_PARAM = "toPSOptionsSpec.pageSize";
	private static final String PAGE_SIZE_HEIGHT_PARAM = "toPSOptionsSpec.pageSizeHeight";
	private static final String PAGE_SIZE_WIDTH_PARAM = "toPSOptionsSpec.pageSizeWidth";
	private static final String PS_LEVEL_PARAM = "toPSOptionsSpec.psLevel";
	private static final String STYLE_PARAM = "toPSOptionsSpec.style";
	private static final String ALLOW_BINARY_CONTENT_PARAM = "toPSOptionsSpec.allowBinaryContent";
	private static final String BLEED_MARKS_PARAM = "toPSOptionsSpec.bleedMarks";
	private static final String COLOR_BARS_PARAM = "toPSOptionsSpec.colorBars";
	private static final String CONVERT_TRUE_TYPE_TO_TYPE1_PARAM = "toPSOptionsSpec.convertTrueTypeToType1";
	private static final String EMIT_CID_FONT_TYPE2_PARAM = "toPSOptionsSpec.emitCIDFontType2";
	private static final String EMIT_PS_FORM_OBJECTS_PARAM = "toPSOptionsSpec.emitPSFormsObjects";
	private static final String EXPAND_TO_FIT_PARAM = "toPSOptionsSpec.expandToFit";
	private static final String PS_INCLUDE_COMMENTS_PARAM = "toPSOptionsSpec.includeComments";
	private static final String LEGACY_TO_SIMPLE_PS_FLAG_PARAM = "toPSOptionsSpec.legacyToSimplePSFlag";
	private static final String PAGE_INFORMATION_PARAM = "toPSOptionsSpec.pageInformation";
	private static final String REGISTRATION_MARKS_PARAM = "toPSOptionsSpec.registrationMarks";
	private static final String REVERSE_PARAM = "toPSOptionsSpec.reverse";
	private static final String ROTATE_AND_CENTER_PARAM = "toPSOptionsSpec.rotateAndCenter";
	private static final String SHRINK_TO_FIT_PARAM = "toPSOptionsSpec.shrinkToFit";
	private static final String TRIM_MARKS_PARAM = "toPSOptionsSpec.trimMarks";
	private static final String USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM = "toPSOptionsSpec.useMaxJPEGImageResolution";

	private final RestClient toImageRestClient;
	private final RestClient toPsRestClient;
	
	// Can only be called from Builder.
	private RestServicesConvertPdfServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
		super(correlationIdFn);
		this.toImageRestClient = builder.createClient(CONVERT_PDF_SERVICE_NAME, TO_IMAGE_METHOD_NAME);
		this.toPsRestClient = builder.createClient(CONVERT_PDF_SERVICE_NAME, TO_PS_METHOD_NAME);
	}

	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc parameter cannot be null.");
		Objects.requireNonNull(toImageOptionsSpec, "ToImageOptionsSpec parameter cannot be null.");
		ImageConvertFormat imageConvertFormat = Objects.requireNonNull(toImageOptionsSpec.getImageConvertFormat(), "ImageConvertFormat cannot be null.");
		
		try (MultipartPayload payload = toImageRestClient.multipartPayloadBuilder()
        												 .add(PDF_PARAM, inPdfDoc, ContentType.APPLICATION_PDF)
        												 .addStringVersion(CMYK_POLICY_PARAM, toImageOptionsSpec.getCmykPolicy())
        												 .addStringVersion(COLOR_COMPRESSION_PARAM, toImageOptionsSpec.getColorCompression())
        												 .addStringVersion(COLOR_SPACE_PARAM, toImageOptionsSpec.getColorSpace())
        												 .addStringVersion(PNG_FILTER_PARAM, toImageOptionsSpec.getFilter())
        												 .addStringVersion(JPEG_FORMAT_PARAM, toImageOptionsSpec.getFormat())
        												 .addStringVersion(GRAY_SCALE_COMPRESSION_PARAM, toImageOptionsSpec.getGrayScaleCompression())
        												 .addStringVersion(GRAY_SCALE_POLICY_PARAM, toImageOptionsSpec.getGrayScalePolicy())
        												 .addStringVersion(IMAGE_CONVERT_FORMAT_PARAM, imageConvertFormat)
        												 .addIfNotNull(IMAGE_SIZE_HEIGHT_PARAM, toImageOptionsSpec.getImageSizeHeight())
        												 .addIfNotNull(IMAGE_SIZE_WIDTH_PARAM, toImageOptionsSpec.getImageSizeWidth())
        												 .addStringVersion(INTERLACE_PARAM, toImageOptionsSpec.getInterlace())
        												 .addStringVersion(MONOCHROME_COMPRESSION_PARAM, toImageOptionsSpec.getMonochrome())
        												 .addStringVersion(MULTI_PAGE_TIFF_PARAM, toImageOptionsSpec.getMultiPageTiff())
        												 .addIfNotNull(IMAGE_PAGE_RANGE_PARAM, toImageOptionsSpec.getPageRange())
        												 .addIfNotNull(RESOLUTION_PARAM, toImageOptionsSpec.getResolution())
        												 .addStringVersion(RGB_POLICY_PARAM, toImageOptionsSpec.getRgbPolicy())
        												 .addStringVersion(ROWS_PER_STRIP_PARAM, toImageOptionsSpec.getRowsPerStrip())
        												 .addStringVersion(TILE_SIZE_PARAM, toImageOptionsSpec.getTileSize())
        												 .addStringVersion(IMAGE_INCLUDE_COMMENTS_PARAM, toImageOptionsSpec.isIncludeComments())
        												 .addStringVersion(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM, toImageOptionsSpec.isUseLegacyImageSizeBehavior())
														 .build()) {
			return payload.postToServer(toContentType(imageConvertFormat))
						  .map(RestServicesConvertPdfServiceAdapter::responseToDocList)
						  .orElseThrow();
		} catch (IOException e) {
			throw new ConvertPdfServiceException("I/O Error while securing document. (" + toImageRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new ConvertPdfServiceException("Error while POSTing to server (" + toImageRestClient.target() + ").", e);
		}


//		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
//			multipart.field(PDF_PARAM, inPdfDoc.getInputStream(), APPLICATION_PDF);
//
//			// This code sets the individual fields if they are not null. 
//			MultipartTransformer.create(multipart)
//								.transform((t)->cmykPolicy == null ? t : t.field(CMYK_POLICY_PARAM, cmykPolicy.toString()))
//								.transform((t)->colorCompression == null ? t : t.field(COLOR_COMPRESSION_PARAM, colorCompression.toString()))
//								.transform((t)->colorSpace == null ? t : t.field(COLOR_SPACE_PARAM, colorSpace.toString()))
//								.transform((t)->filter == null ? t : t.field(PNG_FILTER_PARAM, filter.toString()))
//								.transform((t)->format == null ? t : t.field(JPEG_FORMAT_PARAM, format.toString()))
//								.transform((t)->grayScaleCompression == null ? t : t.field(GRAY_SCALE_COMPRESSION_PARAM, grayScaleCompression.toString()))
//								.transform((t)->grayScalePolicy == null ? t : t.field(GRAY_SCALE_POLICY_PARAM, grayScalePolicy.toString()))
//								.transform((t)->imageConvertFormat == null ? t : t.field(IMAGE_CONVERT_FORMAT_PARAM, imageConvertFormat.toString()))
//								.transform((t)->imageSizeHeight == null ? t : t.field(IMAGE_SIZE_HEIGHT_PARAM, imageSizeHeight))
//								.transform((t)->imageSizeWidth == null ? t : t.field(IMAGE_SIZE_WIDTH_PARAM, imageSizeWidth))
//								.transform((t)->interlace == null ? t : t.field(INTERLACE_PARAM, interlace.toString()))
//								.transform((t)->monochrome == null ? t : t.field(MONOCHROME_COMPRESSION_PARAM, monochrome.toString()))
//								.transform((t)->multiPageTiff == null ? t : t.field(MULTI_PAGE_TIFF_PARAM, multiPageTiff.toString()))
//								.transform((t)->pageRange == null ? t : t.field(IMAGE_PAGE_RANGE_PARAM, pageRange))
//								.transform((t)->resolution == null ? t : t.field(RESOLUTION_PARAM, resolution))
//								.transform((t)->rgbPolicy == null ? t : t.field(RGB_POLICY_PARAM, rgbPolicy.toString()))
//								.transform((t)->rowsPerStrip == null ? t : t.field(ROWS_PER_STRIP_PARAM, rowsPerStrip.toString()))
//								.transform((t)->tileSize == null ? t : t.field(TILE_SIZE_PARAM, tileSize.toString()))
//								.transform((t)->includeComments == null ? t : t.field(IMAGE_INCLUDE_COMMENTS_PARAM, includeComments.toString()))
//								.transform((t)->useLegacyImageSizeBehvior == null ? t : t.field(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM, useLegacyImageSizeBehvior.toString()))
//								;
//			
//			Response result = postToServer(toImageTarget, multipart, MediaType.MULTIPART_FORM_DATA_TYPE);
//			
//			StatusType resultStatus = result.getStatusInfo();
//			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
//				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
//				if (result.hasEntity()) {
//					InputStream entityStream = (InputStream) result.getEntity();
//					message += "\n" + inputStreamtoString(entityStream);
//				}
//				throw new ConvertPdfServiceException(message);
//			}
//			if (!result.hasEntity()) {
//				throw new ConvertPdfServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
//			}
//
//			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
//			if ( responseContentType == null || !MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(MediaType.valueOf(responseContentType))) {
//				String msg = "Response from AEM server was not a multipart form.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
//				InputStream entityStream = (InputStream) result.getEntity();
//				msg += "\n" + inputStreamtoString(entityStream);
//				throw new ConvertPdfServiceException(msg);
//			}
//
//			List<Document> resultDocList = new ArrayList<Document>();
//			FormDataMultiPart resultDoc = result.readEntity(FormDataMultiPart.class);
//			for (Entry<String, List<FormDataBodyPart>> entry : resultDoc.getFields().entrySet()) {
//				String name = entry.getKey();
//				for(FormDataBodyPart part : entry.getValue()) {
//					if (part.isSimple()) {
//						logger.debug("Found simple Form Data Part '" + name + "' (" + part.getName() + ").");
//					} else {
//						logger.debug("Found complex Form Data Part '" + name + "' (" + part.getName() + "/" + part.getContentDisposition() + ").");
//						ContentDisposition contentDisposition = part.getContentDisposition();
//						String fileName = contentDisposition.getFileName();
//						if (fileName != null) {
//							resultDocList.add(SimpleDocumentFactoryImpl.getFactory().create(part.getEntityAs(InputStream.class)));
//						}
//					}
//				}
//			}
//			
////			List<Document> resultDocList = Collections.emptyList();
////			for (BodyPart bodyPart : resultDoc.getBodyParts()) {
////				
////			}
//			return resultDocList;
//		} catch (IOException e) {
//			throw new ConvertPdfServiceException("I/O Error while generating an image. (" + baseTarget.getUri().toString() + ").", e);
//		} catch (RestServicesServiceException e) {
//			throw new ConvertPdfServiceException("Error while POSTing to server", e);
//		}
	}

	private static List<Document> responseToDocList(Response result)  {
		// TODO Set up code that handles one document and produces error for more than one doc.
		return List.of(RestServicesServiceAdapter.responseToDoc(result));
	}

	private static ContentType toContentType(ImageConvertFormat imageConvertFormat) {
		return switch (imageConvertFormat) {
			case JPEG -> ContentType.IMAGE_JPEG;
			case PNG -> ContentType.IMAGE_PNG;
			case TIFF -> ContentType.IMAGE_TIFF;
			case JPEG2K -> ContentType.IMAGE_JPEG;
		};
	}
	
	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc argument cannot be null.");
		Objects.requireNonNull(toPSOptionsSpec, "ToPSOptionsSpec argument cannot be null.");
		
		try (MultipartPayload payload = toImageRestClient.multipartPayloadBuilder()
														 .add(PDF_PARAM, inPdfDoc, ContentType.APPLICATION_PDF)
														 .addStringVersion(COLOR_PARAM, toPSOptionsSpec.getColor())
														 .addStringVersion(FONT_INCLUSION_PARAM, toPSOptionsSpec.getFontInclusion())
														 .addStringVersion(LINE_WEIGHT_PARAM, toPSOptionsSpec.getLineWeight())
														 .addIfNotNull(PS_PAGE_RANGE_PARAM, toPSOptionsSpec.getPageRange())
														 .addStringVersion(PAGE_SIZE_PARAM, toPSOptionsSpec.getPageSize())
														 .addIfNotNull(PAGE_SIZE_HEIGHT_PARAM, toPSOptionsSpec.getPageSizeHeight())
														 .addIfNotNull(PAGE_SIZE_WIDTH_PARAM, toPSOptionsSpec.getPageSizeWidth())
														 .addStringVersion(PS_LEVEL_PARAM, toPSOptionsSpec.getPsLevel())
														 .addStringVersion(STYLE_PARAM, toPSOptionsSpec.getStyle())
														 .addStringVersion(ALLOW_BINARY_CONTENT_PARAM, toPSOptionsSpec.isAllowBinaryContent())
														 .addStringVersion(BLEED_MARKS_PARAM, toPSOptionsSpec.isBleedMarks())
														 .addStringVersion(COLOR_BARS_PARAM, toPSOptionsSpec.isColorBars())
														 .addStringVersion(CONVERT_TRUE_TYPE_TO_TYPE1_PARAM, toPSOptionsSpec.isConvertTrueTypeToType1())
														 .addStringVersion(EMIT_CID_FONT_TYPE2_PARAM, toPSOptionsSpec.isEmitCIDFontType2())
														 .addStringVersion(EMIT_PS_FORM_OBJECTS_PARAM, toPSOptionsSpec.isEmitPSFormObjects())
														 .addStringVersion(EXPAND_TO_FIT_PARAM, toPSOptionsSpec.isExpandToFit())
														 .addStringVersion(PS_INCLUDE_COMMENTS_PARAM, toPSOptionsSpec.isIncludeComments())
														 .addStringVersion(LEGACY_TO_SIMPLE_PS_FLAG_PARAM, toPSOptionsSpec.isLegacyToSimplePSFlag())
														 .addStringVersion(PAGE_INFORMATION_PARAM, toPSOptionsSpec.isPageInformation())
														 .addStringVersion(REGISTRATION_MARKS_PARAM, toPSOptionsSpec.isRegistrationMarks())
														 .addStringVersion(REVERSE_PARAM, toPSOptionsSpec.isReverse())
														 .addStringVersion(ROTATE_AND_CENTER_PARAM, toPSOptionsSpec.isRotateAndCenter())
														 .addStringVersion(SHRINK_TO_FIT_PARAM, toPSOptionsSpec.isShrinkToFit())
														 .addStringVersion(TRIM_MARKS_PARAM, toPSOptionsSpec.isTrimMarks())
														 .addStringVersion(USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM, toPSOptionsSpec.isUseMaxJPEGImageResolution())
				 										 .build()) {
			return payload.postToServer(ContentType.APPLICATION_PS)
						  .map(RestServicesServiceAdapter::responseToDoc)
						  .orElseThrow();
		} catch (IOException e) {
			throw new ConvertPdfServiceException("I/O Error while securing document. (" + toPsRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new ConvertPdfServiceException("Error while POSTing to server (" + toPsRestClient.target() + ").", e);
		}
	}

	/**
	 * Creates a Builder object for building a RestServicesConvertPdfServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static ConvertPdfServiceBuilder builder(RestClientFactory clientFactory) {
		return new ConvertPdfServiceBuilder(clientFactory);
	}
	
	public static class ConvertPdfServiceBuilder implements Builder {
		private BuilderImpl builder;

		private ConvertPdfServiceBuilder(RestClientFactory clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

		@Override
		public ConvertPdfServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public ConvertPdfServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public ConvertPdfServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public ConvertPdfServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public ConvertPdfServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public ConvertPdfServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}

		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesConvertPdfServiceAdapter build() {
			return new RestServicesConvertPdfServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}
}
