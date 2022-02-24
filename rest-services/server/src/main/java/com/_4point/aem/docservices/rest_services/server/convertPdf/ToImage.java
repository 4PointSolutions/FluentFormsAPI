package com._4point.aem.docservices.rest_services.server.convertPdf;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

//import javax.ws.rs.core.MediaType;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
//import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ToImageArgumentBuilder;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com.adobe.fd.cpdf.api.enumeration.CMYKPolicy;
import com.adobe.fd.cpdf.api.enumeration.ColorCompression;
import com.adobe.fd.cpdf.api.enumeration.ColorSpace;
import com.adobe.fd.cpdf.api.enumeration.GrayScaleCompression;
import com.adobe.fd.cpdf.api.enumeration.GrayScalePolicy;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.Interlace;
import com.adobe.fd.cpdf.api.enumeration.JPEGFormat;
import com.adobe.fd.cpdf.api.enumeration.MonochromeCompression;
import com.adobe.fd.cpdf.api.enumeration.PNGFilter;
import com.adobe.fd.cpdf.api.enumeration.RGBPolicy;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.convertPdf.AdobeConvertPdfServiceAdapter;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=ConvertPdfService.ToImage Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/ConvertPdfService/ToImage")
public class ToImage extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(ToImage.class);
	private static final String IMAGE_PARAM = "IMAGE";
	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private final Supplier<TraditionalConvertPdfService> convertPdfServiceFactory = this::getAdobeConvertPdfService;

	@Reference
	private com.adobe.fd.cpdf.api.ConvertPdfService adobeConvertPdfService;
	
	private TraditionalConvertPdfService getAdobeConvertPdfService() {
		return new AdobeConvertPdfServiceAdapter(adobeConvertPdfService);
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
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		ConvertPdfService convertPdfService = new ConvertPdfServiceImpl(convertPdfServiceFactory.get());
		
		ToImageOptionsSpecParameters reqParameters = ToImageOptionsSpecParameters.readToImageOptionsSpecParameters(request);
		Document inPdfDoc = docFactory.create(reqParameters.getInPdfDoc());
		CMYKPolicy cmykPolicy = reqParameters.getCmykPolicy();
		ColorCompression colorCompression = reqParameters.getColorCompression();
		ColorSpace colorSpace = reqParameters.getColorSpace();
		PNGFilter filter = reqParameters.getFilter();
		JPEGFormat format = reqParameters.getFormat();
		GrayScaleCompression grayScaleCompression = reqParameters.getGrayScaleCompression();
		GrayScalePolicy grayScalePolicy = reqParameters.getGrayScalePolicy();
		ImageConvertFormat imageConvertFormat = reqParameters.getImageConvertFormat();
		String imageSizeHeight = reqParameters.getImageSizeHeight();
		String imageSizeWidth = reqParameters.getImageSizeWidth();
		Interlace interlace = reqParameters.getInterlace();
		MonochromeCompression monochrome = reqParameters.getMonochrome();
		Boolean multiPageTiff = reqParameters.getMultiPageTiff();
		String pageRange = reqParameters.getPageRange();
		String resolution = reqParameters.getResolution();
		RGBPolicy rgbPolicy = reqParameters.getRgbPolicy();
		Integer rowsPerStrip = reqParameters.getRowsPerStrip();
		Integer tileSize = reqParameters.getTileSize();
		Boolean includeComments = reqParameters.isIncludeComments();
		Boolean useLegacyImageSizeBehavior = reqParameters.isUseLegacyImageSizeBehavior();
		
		try {
			ToImageArgumentBuilder argBuilder = convertPdfService.toImage()
					.transform(b->cmykPolicy == null ? b : b.setCmykPolicy(cmykPolicy))
					.transform(b->colorCompression == null ? b : b.setColorCompression(colorCompression))
					.transform(b->colorSpace == null ? b : b.setColorSpace(colorSpace))
					.transform(b->filter == null ? b : b.setFilter(filter))
					.transform(b->format == null ? b : b.setFormat(format))
					.transform(b->grayScaleCompression == null ? b : b.setGrayScaleCompression(grayScaleCompression))
					.transform(b->grayScalePolicy == null ? b : b.setGrayScalePolicy(grayScalePolicy))
					.transform(b->b.setImageConvertFormat(imageConvertFormat))
					.transform(b->imageSizeHeight == null ? b : b.setImageSizeHeight(imageSizeHeight))
					.transform(b->imageSizeWidth == null ? b : b.setImageSizeWidth(imageSizeWidth))
					.transform(b->interlace == null ? b : b.setInterlace(interlace))
					.transform(b->monochrome == null ? b : b.setMonochrome(monochrome))
					.transform(b->multiPageTiff == null ? b : b.setMultiPageTiff(multiPageTiff))
					.transform(b->pageRange == null ? b : b.setPageRange(pageRange))
					.transform(b->resolution == null ? b : b.setResolution(resolution))
					.transform(b->rgbPolicy == null ? b : b.setRgbPolicy(rgbPolicy))
					.transform(b->rowsPerStrip == null ? b : b.setRowsPerStrip(rowsPerStrip))
					.transform(b->tileSize == null ? b : b.setTileSize(tileSize))
					.transform(b->includeComments == null ? b : b.setIncludeComments(includeComments))
					.transform(b->useLegacyImageSizeBehavior == null ? b : b.setUseLegacyImageSizeBehavior(useLegacyImageSizeBehavior));

			List<Document> result = argBuilder.executeOn(inPdfDoc);
			if (result.size() == 1) {
				String contentType = result.get(0).getContentType();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				ServletUtils.transfer(result.get(0).getInputStream(), response.getOutputStream());
			}
			else if (result.size() == 0) {
				throw new ConvertPdfServiceException("The returned List<Document> was empty.");
			}
			else {
//				FormDataMultiPart multipart = createMultipartFormResponseStream(result);
				// How do you get the multipart into the response?
				throw new UnsupportedOperationException("Multipart form response not yet available.");
			}
		} catch (ConvertPdfServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while converting a PDF to an image. (" + ex1.getMessage() + ").", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while converting a PDF to an image.", ex2);
		}
	}
	
//	private MediaType getMediaType(String contentType) throws ConvertPdfServiceException {
//		if (contentType.contains("/jpeg")) {
//			return new MediaType("image", "jpeg");
//		} else if (contentType.contains("/jp2")) {
//			return new MediaType("image", "jp2");
//		} else if (contentType.contains("/png")) {
//			return new MediaType("image", "png");
//		} else if (contentType.contains("/tiff")) {
//			return new MediaType("image", "tiff");
//		} else {
//			throw new ConvertPdfServiceException("Unknown image media type.");
//		}
//	}

//	private Document executeOn(Document inPdfDoc, ToImageArgumentBuilder argBuilder) throws ConvertPdfServiceException {
//		return argBuilder.executeOn(inPdfDoc);
//	}
//	
//	private FormDataMultiPart createMultipartFormResponseStream(List<Document> result) throws ConvertPdfServiceException, IOException {
//		int i = 1;
//		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
//			for (Document page : result) {
//				// Create a multipart form response
//				multipart.field(IMAGE_PARAM + i, SimpleDocumentFactoryImpl.getFactory().create(page.getInputStream()), getMediaType(page.getContentType()));
//				i++;
//			}
//			return multipart;
//		}
//	}
	
	private static class ToImageOptionsSpecParameters {
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
		private static final String INCLUDE_COMMENTS_PARAM = "toImageOptionsSpec.includeComments";
		private static final String INTERLACE_PARAM = "toImageOptionsSpec.interlace";
		private static final String MONOCHROME_COMPRESSION_PARAM = "toImageOptionsSpec.monochrome";
		private static final String MULTI_PAGE_TIFF_PARAM = "toImageOptionsSpec.multiPageTiff";
		private static final String PAGE_RANGE_PARAM = "toImageOptionsSpec.pageRange";
		private static final String RESOLUTION_PARAM = "toImageOptionsSpec.resolution";
		private static final String RGB_POLICY_PARAM = "toImageOptionsSpec.rgbPolicy";
		private static final String ROWS_PER_STRIP_PARAM = "toImageOptionsSpec.rowsPerStrip";
		private static final String TILE_SIZE_PARAM = "toImageOptionsSpec.tileSize";
		private static final String USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM = "toImageOptionsSpec.useLegacyImageSizeBehavior";
		
		private CMYKPolicy cmykPolicy = null;
		private ColorCompression colorCompression = null;
		private ColorSpace colorSpace = null;
		private PNGFilter filter = null;
		private JPEGFormat format = null;
		private GrayScaleCompression grayScaleCompression = null;
		private GrayScalePolicy grayScalePolicy = null;
		private ImageConvertFormat imageConvertFormat = null;
		private String imageSizeHeight = null;
		private String imageSizeWidth = null;
		private Interlace interlace = null;
		private MonochromeCompression monochrome = null;
		private Boolean multiPageTiff = null;
		private String pageRange = null;
		private String resolution = null;
		private RGBPolicy rgbPolicy = null;
		private Integer rowsPerStrip = null;
		private Integer tileSize = null;
		private Boolean includeComments = null;
		private Boolean useLegacyImageSizeBehavior = null;
		private final byte[] inPdfDoc;
		
		public ToImageOptionsSpecParameters(byte[] inPdfDoc) {
			super();
			this.inPdfDoc = inPdfDoc;
		}
		
		public CMYKPolicy getCmykPolicy() {
			return cmykPolicy;
		}
		
		private ToImageOptionsSpecParameters setCmykPolicy(String cmykPolicy) {
			this.cmykPolicy = CMYKPolicy.valueOf(cmykPolicy);
			return this;
		}
		
		public ColorCompression getColorCompression() {
			return colorCompression;
		}
		
		private ToImageOptionsSpecParameters setColorCompression(String colorCompression) {
			this.colorCompression = ColorCompression.valueOf(colorCompression);
			return this;
		}
		
		public ColorSpace getColorSpace() {
			return colorSpace;
		}
		
		private ToImageOptionsSpecParameters setColorSpace(String colorSpace) {
			this.colorSpace = ColorSpace.valueOf(colorSpace);
			return this;
		}
		
		public PNGFilter getFilter() {
			return filter;
		}
		
		private ToImageOptionsSpecParameters setFilter(String filter) {
			this.filter = PNGFilter.valueOf(filter);
			return this;
		}
		
		public JPEGFormat getFormat() {
			return format;
		}
		
		private ToImageOptionsSpecParameters setFormat(String format) {
			this.format = JPEGFormat.valueOf(format);
			return this;
		}
		
		public GrayScaleCompression getGrayScaleCompression() {
			return grayScaleCompression;
		}
		
		private ToImageOptionsSpecParameters setGrayScaleCompression(String grayScaleCompression) {
			this.grayScaleCompression = GrayScaleCompression.valueOf(grayScaleCompression);
			return this;
		}
		
		public GrayScalePolicy getGrayScalePolicy() {
			return grayScalePolicy;
		}
		
		private ToImageOptionsSpecParameters setGrayScalePolicy(String grayScalePolicy) {
			this.grayScalePolicy = GrayScalePolicy.valueOf(grayScalePolicy);
			return this;
		}
		
		public ImageConvertFormat getImageConvertFormat() {
			return imageConvertFormat;
		}
		
		private ToImageOptionsSpecParameters setImageConvertFormat(String imageConvertFormat) {
			this.imageConvertFormat = ImageConvertFormat.valueOf(imageConvertFormat);
			return this;
		}
		
		public String getImageSizeHeight() {
			return imageSizeHeight;
		}
		
		private ToImageOptionsSpecParameters setImageSizeHeight(String imageSizeHeight) {
			this.imageSizeHeight = imageSizeHeight;
			return this;
		}
		
		public String getImageSizeWidth() {
			return imageSizeWidth;
		}
		
		private ToImageOptionsSpecParameters setImageSizeWidth(String imageSizeWidth) {
			this.imageSizeWidth = imageSizeWidth;
			return this;
		}
		
		public Interlace getInterlace() {
			return interlace;
		}
		
		private ToImageOptionsSpecParameters setInterlace(String interlace) {
			this.interlace = Interlace.valueOf(interlace);
			return this;
		}
		
		public MonochromeCompression getMonochrome() {
			return monochrome;
		}
		
		private ToImageOptionsSpecParameters setMonochrome(String monochrome) {
			this.monochrome = MonochromeCompression.valueOf(monochrome);
			return this;
		}
		
		public Boolean getMultiPageTiff() {
			return multiPageTiff;
		}
		
		private ToImageOptionsSpecParameters setMultiPageTiff(String multiPageTiff) {
			this.multiPageTiff = Boolean.valueOf(multiPageTiff);
			return this;
		}
		
		public String getPageRange() {
			return pageRange;
		}
		
		private ToImageOptionsSpecParameters setPageRange(String pageRange) {
			this.pageRange = pageRange;
			return this;
		}
		
		public String getResolution() {
			return resolution;
		}
		
		private ToImageOptionsSpecParameters setResolution(String resolution) {
			this.resolution = resolution;
			return this;
		}
		
		public RGBPolicy getRgbPolicy() {
			return rgbPolicy;
		}
		
		private ToImageOptionsSpecParameters setRgbPolicy(String rgbPolicy) {
			this.rgbPolicy = RGBPolicy.valueOf(rgbPolicy);
			return this;
		}
		
		public Integer getRowsPerStrip() {
			return rowsPerStrip;
		}
		
		private ToImageOptionsSpecParameters setRowsPerStrip(String rowsPerStrip) {
			this.rowsPerStrip = Integer.valueOf(rowsPerStrip);
			return this;
		}
		
		public Integer getTileSize() {
			return tileSize;
		}
		
		private ToImageOptionsSpecParameters setTileSize(String tileSize) {
			this.tileSize = Integer.valueOf(tileSize);
			return this;
		}
		
		public Boolean isIncludeComments() {
			return includeComments;
		}
		
		private ToImageOptionsSpecParameters setIncludeComments(String includeComments) {
			this.includeComments = Boolean.valueOf(includeComments);
			return this;
		}
		
		public Boolean isUseLegacyImageSizeBehavior() {
			return useLegacyImageSizeBehavior;
		}
		
		private ToImageOptionsSpecParameters setUseLegacyImageSizeBehavior(String useLegacyImageSizeBehavior) {
			this.useLegacyImageSizeBehavior = Boolean.valueOf(useLegacyImageSizeBehavior);
			return this;
		}
		
		public byte[] getInPdfDoc() {
			return inPdfDoc;
		}

		public static ToImageOptionsSpecParameters readToImageOptionsSpecParameters(SlingHttpServletRequest request) throws BadRequestException {
			try {
				byte[] inPdfDoc = getMandatoryParameter(request, PDF_PARAM).get();
				
				ToImageOptionsSpecParameters result = new ToImageOptionsSpecParameters(inPdfDoc);
				
				getOptionalParameter(request, CMYK_POLICY_PARAM).ifPresent(rp->result.setCmykPolicy(rp.getString()));
				getOptionalParameter(request, COLOR_COMPRESSION_PARAM).ifPresent(rp->result.setColorCompression(rp.getString()));
				getOptionalParameter(request, COLOR_SPACE_PARAM).ifPresent(rp->result.setColorSpace(rp.getString()));
				getOptionalParameter(request, PNG_FILTER_PARAM).ifPresent(rp->result.setFilter(rp.getString()));
				getOptionalParameter(request, JPEG_FORMAT_PARAM).ifPresent(rp->result.setFormat(rp.getString()));
				getOptionalParameter(request, GRAY_SCALE_COMPRESSION_PARAM).ifPresent(rp->result.setGrayScaleCompression(rp.getString()));
				getOptionalParameter(request, GRAY_SCALE_POLICY_PARAM).ifPresent(rp->result.setGrayScalePolicy(rp.getString()));
				result.setImageConvertFormat(getMandatoryParameter(request, IMAGE_CONVERT_FORMAT_PARAM).getString());
				getOptionalParameter(request, IMAGE_SIZE_HEIGHT_PARAM).ifPresent(rp->result.setImageSizeHeight(rp.getString()));
				getOptionalParameter(request, IMAGE_SIZE_WIDTH_PARAM).ifPresent(rp->result.setImageSizeWidth(rp.getString()));
				getOptionalParameter(request, INCLUDE_COMMENTS_PARAM).ifPresent(rp->result.setIncludeComments(rp.getString()));
				getOptionalParameter(request, INTERLACE_PARAM).ifPresent(rp->result.setInterlace(rp.getString()));
				getOptionalParameter(request, MONOCHROME_COMPRESSION_PARAM).ifPresent(rp->result.setMonochrome(rp.getString()));
				getOptionalParameter(request, MULTI_PAGE_TIFF_PARAM).ifPresent(rp->result.setMultiPageTiff(rp.getString()));
				getOptionalParameter(request, PAGE_RANGE_PARAM).ifPresent(rp->result.setPageRange(rp.getString()));
				getOptionalParameter(request, RESOLUTION_PARAM).ifPresent(rp->result.setResolution(rp.getString()));
				getOptionalParameter(request, RGB_POLICY_PARAM).ifPresent(rp->result.setRgbPolicy(rp.getString()));
				getOptionalParameter(request, ROWS_PER_STRIP_PARAM).ifPresent(rp->result.setRowsPerStrip(rp.getString()));
				getOptionalParameter(request, TILE_SIZE_PARAM).ifPresent(rp->result.setTileSize(rp.getString()));
				getOptionalParameter(request, USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM).ifPresent(rp->result.setUseLegacyImageSizeBehavior(rp.getString()));
				
				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}
	}
}

