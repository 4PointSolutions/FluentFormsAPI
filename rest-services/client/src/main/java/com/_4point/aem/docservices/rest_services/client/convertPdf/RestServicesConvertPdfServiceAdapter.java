package com._4point.aem.docservices.rest_services.client.convertPdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com.adobe.fd.cpdf.api.enumeration.CMYKPolicy;
import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.ColorCompression;
import com.adobe.fd.cpdf.api.enumeration.ColorSpace;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.GrayScaleCompression;
import com.adobe.fd.cpdf.api.enumeration.GrayScalePolicy;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.Interlace;
import com.adobe.fd.cpdf.api.enumeration.JPEGFormat;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.MonochromeCompression;
import com.adobe.fd.cpdf.api.enumeration.PNGFilter;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.RGBPolicy;
import com.adobe.fd.cpdf.api.enumeration.Style;

public class RestServicesConvertPdfServiceAdapter extends RestServicesServiceAdapter implements TraditionalConvertPdfService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int DEFAULT_BUFFER_SIZE = 8192 * 4;	// 32Kb buffer
	
	private static final MediaType APPLICATION_JPEG = new MediaType("image", "jpg");
	private static final MediaType APPLICATION_PS = new MediaType("application", "postscript");
	private static final String CONVERT_PDF_SERVICE_NAME = "ConvertPdfService";
	private static final String TO_IMAGE_METHOD_NAME = "ToImage";
	private static final String IMAGE_PARAM = "IMAGE";
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

	// Can only be called from Builder.
	private RestServicesConvertPdfServiceAdapter(WebTarget baseTarget, Supplier<String> correlationIdFn, AemServerType aemServerType) {
		super(baseTarget, correlationIdFn, aemServerType);
	}

	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		WebTarget toImageTarget = baseTarget.path(constructStandardPath(CONVERT_PDF_SERVICE_NAME, TO_IMAGE_METHOD_NAME));
		if (inPdfDoc == null) {
			throw new NullPointerException("inPdfDoc cannot be null.");
		}
		Objects.requireNonNull(toImageOptionsSpec, "ToImageOptionsSpec argument cannot be null.");
		
		CMYKPolicy cmykPolicy = toImageOptionsSpec.getCmykPolicy();
		ColorCompression colorCompression = toImageOptionsSpec.getColorCompression();
		ColorSpace colorSpace = toImageOptionsSpec.getColorSpace();
		PNGFilter filter = toImageOptionsSpec.getFilter();
		JPEGFormat format = toImageOptionsSpec.getFormat();
		GrayScaleCompression grayScaleCompression = toImageOptionsSpec.getGrayScaleCompression();
		GrayScalePolicy grayScalePolicy = toImageOptionsSpec.getGrayScalePolicy();
		ImageConvertFormat imageConvertFormat = toImageOptionsSpec.getImageConvertFormat();
		Objects.requireNonNull(imageConvertFormat, "ImageConvertFormat cannot be null.");
		String imageSizeHeight = toImageOptionsSpec.getImageSizeHeight();
		String imageSizeWidth = toImageOptionsSpec.getImageSizeWidth();
		Interlace interlace = toImageOptionsSpec.getInterlace();
		MonochromeCompression monochrome = toImageOptionsSpec.getMonochrome();
		Boolean multiPageTiff = toImageOptionsSpec.getMultiPageTiff();
		String pageRange = toImageOptionsSpec.getPageRange();
		String resolution = toImageOptionsSpec.getResolution();
		RGBPolicy rgbPolicy = toImageOptionsSpec.getRgbPolicy();
		Integer rowsPerStrip = toImageOptionsSpec.getRowsPerStrip();
		Integer tileSize = toImageOptionsSpec.getTileSize();
		Boolean includeComments = toImageOptionsSpec.isIncludeComments();
		Boolean useLegacyImageSizeBehvior = toImageOptionsSpec.isUseLegacyImageSizeBehavior();
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(PDF_PARAM, inPdfDoc.getInputStream(), APPLICATION_PDF);

			// This code sets the individual fields if they are not null. 
			MultipartTransformer.create(multipart)
								.transform((t)->cmykPolicy == null ? t : t.field(CMYK_POLICY_PARAM, cmykPolicy.toString()))
								.transform((t)->colorCompression == null ? t : t.field(COLOR_COMPRESSION_PARAM, colorCompression.toString()))
								.transform((t)->colorSpace == null ? t : t.field(COLOR_SPACE_PARAM, colorSpace.toString()))
								.transform((t)->filter == null ? t : t.field(PNG_FILTER_PARAM, filter.toString()))
								.transform((t)->format == null ? t : t.field(JPEG_FORMAT_PARAM, format.toString()))
								.transform((t)->grayScaleCompression == null ? t : t.field(GRAY_SCALE_COMPRESSION_PARAM, grayScaleCompression.toString()))
								.transform((t)->grayScalePolicy == null ? t : t.field(GRAY_SCALE_POLICY_PARAM, grayScalePolicy.toString()))
								.transform((t)->imageConvertFormat == null ? t : t.field(IMAGE_CONVERT_FORMAT_PARAM, imageConvertFormat.toString()))
								.transform((t)->imageSizeHeight == null ? t : t.field(IMAGE_SIZE_HEIGHT_PARAM, imageSizeHeight))
								.transform((t)->imageSizeWidth == null ? t : t.field(IMAGE_SIZE_WIDTH_PARAM, imageSizeWidth))
								.transform((t)->interlace == null ? t : t.field(INTERLACE_PARAM, interlace.toString()))
								.transform((t)->monochrome == null ? t : t.field(MONOCHROME_COMPRESSION_PARAM, monochrome.toString()))
								.transform((t)->multiPageTiff == null ? t : t.field(MULTI_PAGE_TIFF_PARAM, multiPageTiff.toString()))
								.transform((t)->pageRange == null ? t : t.field(IMAGE_PAGE_RANGE_PARAM, pageRange))
								.transform((t)->resolution == null ? t : t.field(RESOLUTION_PARAM, resolution))
								.transform((t)->rgbPolicy == null ? t : t.field(RGB_POLICY_PARAM, rgbPolicy.toString()))
								.transform((t)->rowsPerStrip == null ? t : t.field(ROWS_PER_STRIP_PARAM, rowsPerStrip.toString()))
								.transform((t)->tileSize == null ? t : t.field(TILE_SIZE_PARAM, tileSize.toString()))
								.transform((t)->includeComments == null ? t : t.field(IMAGE_INCLUDE_COMMENTS_PARAM, includeComments.toString()))
								.transform((t)->useLegacyImageSizeBehvior == null ? t : t.field(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM, useLegacyImageSizeBehvior.toString()))
								;
			
			Response result = postToServer(toImageTarget, multipart, MediaType.MULTIPART_FORM_DATA_TYPE);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new ConvertPdfServiceException(message);
			}
			if (!result.hasEntity()) {
				throw new ConvertPdfServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a multipart form.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new ConvertPdfServiceException(msg);
			}

			List<Document> resultDocList = new ArrayList<Document>();
			FormDataMultiPart resultDoc = result.readEntity(FormDataMultiPart.class);
			for (Entry<String, List<FormDataBodyPart>> entry : resultDoc.getFields().entrySet()) {
				String name = entry.getKey();
				for(FormDataBodyPart part : entry.getValue()) {
					if (part.isSimple()) {
						logger.debug("Found simple Form Data Part '" + name + "' (" + part.getName() + ").");
					} else {
						logger.debug("Found complex Form Data Part '" + name + "' (" + part.getName() + "/" + part.getContentDisposition() + ").");
						ContentDisposition contentDisposition = part.getContentDisposition();
						String fileName = contentDisposition.getFileName();
						if (fileName != null) {
							resultDocList.add(SimpleDocumentFactoryImpl.getFactory().create(part.getEntityAs(InputStream.class)));
						}
					}
				}
			}
			
//			List<Document> resultDocList = Collections.emptyList();
//			for (BodyPart bodyPart : resultDoc.getBodyParts()) {
//				
//			}
			return resultDocList;
		} catch (IOException e) {
			throw new ConvertPdfServiceException("I/O Error while generating an image. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new ConvertPdfServiceException("Error while POSTing to server", e);
		}
	}
//	
//	private static byte[] readAllBytes(InputStream is) throws IOException {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//        transfer(is, out);
//        return out.toByteArray();
//	}
//	
//	private static void transfer(InputStream is, OutputStream out) throws IOException {
//		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//        int read;
//        while ((read = is.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
//            out.write(buffer, 0, read);
//        }
//	}

	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		WebTarget toImageTarget = baseTarget.path(constructStandardPath(CONVERT_PDF_SERVICE_NAME, TO_PS_METHOD_NAME));
		if (inPdfDoc == null) {
			throw new NullPointerException("inPdfDoc cannot be null.");
		}
		Objects.requireNonNull(toPSOptionsSpec, "ToPSOptionsSpec Argument cannot be null.");
		
		Color color = toPSOptionsSpec.getColor();
		FontInclusion fontInclusion = toPSOptionsSpec.getFontInclusion();
		LineWeight lineWeight = toPSOptionsSpec.getLineWeight();
		String pageRange = toPSOptionsSpec.getPageRange();
		PageSize pageSize = toPSOptionsSpec.getPageSize();
		String pageSizeHeight = toPSOptionsSpec.getPageSizeHeight();
		String pageSizeWidth = toPSOptionsSpec.getPageSizeWidth();
		PSLevel psLevel = toPSOptionsSpec.getPsLevel();
		Style style = toPSOptionsSpec.getStyle();
		Boolean allowBinaryContent = toPSOptionsSpec.isAllowBinaryContent();
		Boolean bleedMarks = toPSOptionsSpec.isBleedMarks();
		Boolean colorBars = toPSOptionsSpec.isColorBars();
		Boolean convertTrueTypeToType1 = toPSOptionsSpec.isConvertTrueTypeToType1();
		Boolean emitCIDFontType2 = toPSOptionsSpec.isEmitCIDFontType2();
		Boolean emitPSFormObjects = toPSOptionsSpec.isEmitPSFormObjects();
		Boolean expandToFit = toPSOptionsSpec.isExpandToFit();
		Boolean includeComments = toPSOptionsSpec.isIncludeComments();
		Boolean legacyToSimplePSFlag = toPSOptionsSpec.isLegacyToSimplePSFlag();
		Boolean pageInformation = toPSOptionsSpec.isPageInformation();
		Boolean registrationMarks = toPSOptionsSpec.isRegistrationMarks();
		Boolean reverse = toPSOptionsSpec.isReverse();
		Boolean rotateAndCenter = toPSOptionsSpec.isRotateAndCenter();
		Boolean shrinkToFit = toPSOptionsSpec.isShrinkToFit();
		Boolean trimMarks = toPSOptionsSpec.isTrimMarks();
		Boolean useMaxJPEGImageResolution = toPSOptionsSpec.isUseMaxJPEGImageResolution();
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(PDF_PARAM, inPdfDoc.getInputStream(), APPLICATION_PDF);

			// This code sets the individual fields if they are not null. 
			MultipartTransformer.create(multipart)
								.transform((t)->color == null ? t : t.field(COLOR_PARAM, color.toString()))
								.transform((t)->fontInclusion == null ? t : t.field(FONT_INCLUSION_PARAM, fontInclusion.toString()))
								.transform((t)->lineWeight == null ? t : t.field(LINE_WEIGHT_PARAM, lineWeight.toString()))
								.transform((t)->pageRange == null ? t : t.field(PS_PAGE_RANGE_PARAM, pageRange))
								.transform((t)->pageSize == null ? t : t.field(PAGE_SIZE_PARAM, pageSize.toString()))
								.transform((t)->pageSizeHeight == null ? t : t.field(PAGE_SIZE_HEIGHT_PARAM, pageSizeHeight))
								.transform((t)->pageSizeWidth == null ? t : t.field(PAGE_SIZE_WIDTH_PARAM, pageSizeWidth))
								.transform((t)->psLevel == null ? t : t.field(PS_LEVEL_PARAM, psLevel.toString()))
								.transform((t)->style == null ? t : t.field(STYLE_PARAM, style.toString()))
								.transform((t)->allowBinaryContent == null ? t : t.field(ALLOW_BINARY_CONTENT_PARAM, allowBinaryContent.toString()))
								.transform((t)->bleedMarks == null ? t : t.field(BLEED_MARKS_PARAM, bleedMarks.toString()))
								.transform((t)->colorBars == null ? t : t.field(COLOR_BARS_PARAM, colorBars.toString()))
								.transform((t)->convertTrueTypeToType1 == null ? t : t.field(CONVERT_TRUE_TYPE_TO_TYPE1_PARAM, convertTrueTypeToType1.toString()))
								.transform((t)->emitCIDFontType2 == null ? t : t.field(EMIT_CID_FONT_TYPE2_PARAM, emitCIDFontType2.toString()))
								.transform((t)->emitPSFormObjects == null ? t : t.field(EMIT_PS_FORM_OBJECTS_PARAM, emitPSFormObjects.toString()))
								.transform((t)->expandToFit == null ? t : t.field(EXPAND_TO_FIT_PARAM, expandToFit.toString()))
								.transform((t)->includeComments == null ? t : t.field(PS_INCLUDE_COMMENTS_PARAM, includeComments.toString()))
								.transform((t)->legacyToSimplePSFlag == null ? t : t.field(LEGACY_TO_SIMPLE_PS_FLAG_PARAM, legacyToSimplePSFlag.toString()))
								.transform((t)->pageInformation == null ? t : t.field(PAGE_INFORMATION_PARAM, pageInformation.toString()))
								.transform((t)->registrationMarks == null ? t : t.field(REGISTRATION_MARKS_PARAM, registrationMarks.toString()))
								.transform((t)->reverse == null ? t : t.field(REVERSE_PARAM, reverse.toString()))
								.transform((t)->rotateAndCenter == null ? t : t.field(ROTATE_AND_CENTER_PARAM, rotateAndCenter.toString()))
								.transform((t)->shrinkToFit == null ? t : t.field(SHRINK_TO_FIT_PARAM, shrinkToFit.toString()))
								.transform((t)->trimMarks == null ? t : t.field(TRIM_MARKS_PARAM, trimMarks.toString()))
								.transform((t)->useMaxJPEGImageResolution == null ? t : t.field(USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM, useMaxJPEGImageResolution.toString()));
			
			Response result = postToServer(toImageTarget, multipart, APPLICATION_PS);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new ConvertPdfServiceException(message);
			}
			if (!result.hasEntity()) {
				throw new ConvertPdfServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_PS.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not PostScript.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new ConvertPdfServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PS.toString());
			return resultDoc;
		} catch (IOException e) {
			throw new ConvertPdfServiceException("I/O Error while generating PostScript output. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new ConvertPdfServiceException("Error while POSTing to server", e);
		}
	}

	/**
	 * Creates a Builder object for building a RestServicesConvertPdfServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static ConvertPdfServiceBuilder builder() {
		return new ConvertPdfServiceBuilder();
	}
	
	public static class ConvertPdfServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();

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
		public ConvertPdfServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
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
			return new RestServicesConvertPdfServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn(), this.getAemServerType());
		}
	}
}
