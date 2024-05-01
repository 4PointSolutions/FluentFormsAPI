package com._4point.aem.docservices.rest_services.server.convertPdf;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

import java.io.IOException;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ToPSArgumentBuilder;
import com._4point.aem.fluentforms.impl.convertPdf.AdobeConvertPdfServiceAdapter;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.Style;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=ConvertPdfService.ToPS Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/ConvertPdfService/ToPS")
public class ToPS extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(ToPS.class);
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
		
		ToPSOptionsSpecParameters reqParameters = ToPSOptionsSpecParameters.readToPSOptionsSpecParameters(request);
		Document inPdfDoc = docFactory.create(reqParameters.getInPdfDoc());
		Color color = reqParameters.getColor();
		FontInclusion fontInclusion = reqParameters.getFontInclusion();
		LineWeight lineWeight = reqParameters.getLineWeight();
		String pageRange = reqParameters.getPageRange();
		PageSize pageSize = reqParameters.getPageSize();
		String pageSizeHeight = reqParameters.getPageSizeHeight();
		String pageSizeWidth = reqParameters.getPageSizeWidth();
		PSLevel psLevel = reqParameters.getPsLevel();
		Style style = reqParameters.getStyle();
		Boolean allowBinaryContent = reqParameters.getAllowBinaryContent();
		Boolean bleedMarks = reqParameters.getBleedMarks();
		Boolean colorBars = reqParameters.getColorBars();
		Boolean convertTrueTypeToType1 = reqParameters.getConvertTrueTypeToType1();
		Boolean emitCIDFontType2 = reqParameters.getEmitCIDFontType2();
		Boolean emitPSFormObjects = reqParameters.getEmitPSFormObjects();
		Boolean expandToFit = reqParameters.getExpandToFit();
		Boolean includeComments = reqParameters.getIncludeComments();
		Boolean legacyToSimplePSFlag = reqParameters.getLegacyToSimplePSFlag();
		Boolean pageInformation = reqParameters.getPageInformation();
		Boolean registrationMarks = reqParameters.getRegistrationMarks();
		Boolean reverse = reqParameters.getReverse();
		Boolean rotateAndCenter = reqParameters.getRotateAndCenter();
		Boolean shrinkToFit = reqParameters.getShrinkToFit();
		Boolean trimMarks = reqParameters.getTrimMarks();
		Boolean useMaxJPEGImageResolution = reqParameters.getUseMaxJPEGImageResolution();
		
		try {
			ToPSArgumentBuilder argBuilder = convertPdfService.toPS()
					.transform(b->color == null ? b : b.setColor(color))
					.transform(b->fontInclusion == null ? b : b.setFontInclusion(fontInclusion))
					.transform(b->lineWeight == null ? b : b.setLineWeight(lineWeight))
					.transform(b->pageRange == null ? b : b.setPageRange(pageRange))
					.transform(b->pageSize == null ? b : b.setPageSize(pageSize))
					.transform(b->pageSizeHeight == null ? b : b.setPageSizeHeight(pageSizeHeight))
					.transform(b->pageSizeWidth == null ? b : b.setPageSizeWidth(pageSizeWidth))
					.transform(b->psLevel == null ? b : b.setPsLevel(psLevel))
					.transform(b->style == null ? b : b.setStyle(style))
					.transform(b->allowBinaryContent == null ? b : b.setAllowedBinaryContent(allowBinaryContent))
					.transform(b->bleedMarks == null ? b : b.setBleedMarks(bleedMarks))
					.transform(b->colorBars == null ? b : b.setColorBars(colorBars))
					.transform(b->convertTrueTypeToType1 == null ? b : b.setConvertTrueTypeToType1(convertTrueTypeToType1))
					.transform(b->emitCIDFontType2 == null ? b : b.setEmitCIDFontType2(emitCIDFontType2))
					.transform(b->emitPSFormObjects == null ? b : b.setEmitPSFormObjects(emitPSFormObjects))
					.transform(b->expandToFit == null ? b : b.setExpandToFit(expandToFit))
					.transform(b->includeComments == null ? b : b.setIncludeComments(includeComments))
					.transform(b->legacyToSimplePSFlag == null ? b : b.setLegacyToSimplePSFlag(legacyToSimplePSFlag))
					.transform(b->pageInformation == null ? b : b.setPageInformation(pageInformation))
					.transform(b->registrationMarks == null ? b : b.setRegistrationMarks(registrationMarks))
					.transform(b->reverse == null ? b : b.setReverse(reverse))
					.transform(b->rotateAndCenter == null ? b : b.setRotateAndCenter(rotateAndCenter))
					.transform(b->shrinkToFit == null ? b : b.setShrinkToFit(shrinkToFit))
					.transform(b->trimMarks == null ? b : b.setTrimMarks(trimMarks))
					.transform(b->useMaxJPEGImageResolution == null ? b : b.setUseMaxJPEGImageResolution(useMaxJPEGImageResolution));
			
			try (Document result = argBuilder.executeOn(inPdfDoc)) {
				ServletUtils.transferDocumentToResponse(request, response, result, false);
			}
		} catch (ConvertPdfServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while converting a PDF to PostScript. (" + ex1.getMessage() + ").", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while converting a PDF to PostScript.", ex2);
		}
	}
	
	private static class ToPSOptionsSpecParameters {
		private static final String PDF_PARAM = "inPdfDoc";
		private static final String ALLOW_BINARY_CONTENT_PARAM = "toPSOptionsSpec.allowBinaryContent";
		private static final String BLEED_MARKS_PARAM = "toPSOptionsSpec.bleedMarks";
		private static final String COLOR_PARAM = "toPSOptionsSpec.color";
		private static final String COLOR_BARS_PARAM = "toPSOptionsSpec.colorBars";
		private static final String CONVERT_TRUE_TYPE_TO_TYPE1_PARAM = "toPSOptionsSpec.convertTrueTypeToType1";
		private static final String EMIT_CID_FONT_TYPE2_PARAM = "toPSOptionsSpec.emitCIDFontType2";
		private static final String EMIT_PS_FORM_OBJECTS_PARAM = "toPSOptionsSpec.emitPSFormsObjects";
		private static final String EXPAND_TO_FIT_PARAM = "toPSOptionsSpec.expandToFit";
		private static final String FONT_INCLUSION_PARAM = "toPSOptionsSpec.fontInclusion";
		private static final String INCLUDE_COMMENTS_PARAM = "toPSOptionsSpec.includeComments";
		private static final String LEGACY_TO_SIMPLE_PS_FLAG_PARAM = "toPSOptionsSpec.legacyToSimplePSFlag";
		private static final String LINE_WEIGHT_PARAM = "toPSOptionsSpec.lineWeight";
		private static final String PAGE_INFORMATION_PARAM = "toPSOptionsSpec.pageInformation";
		private static final String PAGE_RANGE_PARAM = "toPSOptionsSpec.pageRange";
		private static final String PAGE_SIZE_PARAM = "toPSOptionsSpec.pageSize";
		private static final String PAGE_SIZE_HEIGHT_PARAM = "toPSOptionsSpec.pageSizeHeight";
		private static final String PAGE_SIZE_WIDTH_PARAM = "toPSOptionsSpec.pageSizeWidth";
		private static final String PS_LEVEL_PARAM = "toPSOptionsSpec.psLevel";
		private static final String REGISTRATION_MARKS_PARAM = "toPSOptionsSpec.registrationMarks";
		private static final String REVERSE_PARAM = "toPSOptionsSpec.reverse";
		private static final String ROTATE_AND_CENTER_PARAM = "toPSOptionsSpec.rotateAndCenter";
		private static final String SHRINK_TO_FIT_PARAM = "toPSOptionsSpec.shrinkToFit";
		private static final String STYLE_PARAM = "toPSOptionsSpec.style";
		private static final String TRIM_MARKS_PARAM = "toPSOptionsSpec.trimMarks";
		private static final String USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM = "toPSOptionsSpec.useMaxJPEGImageResolution";

		
		private Color color = null;
		private FontInclusion fontInclusion = null;
		private LineWeight lineWeight = null;
		private String pageRange = null;
		private PageSize pageSize = null;
		private String pageSizeHeight = null;
		private String pageSizeWidth = null;
		private PSLevel psLevel = null;
		private Style style = null;
		private Boolean allowBinaryContent = null;
		private Boolean bleedMarks = null;
		private Boolean colorBars = null;
		private Boolean convertTrueTypeToType1 = null;
		private Boolean emitCIDFontType2 = null;
		private Boolean emitPSFormObjects = null;
		private Boolean expandToFit = null;
		private Boolean includeComments = null;
		private Boolean legacyToSimplePSFlag = null;
		private Boolean pageInformation = null;
		private Boolean registrationMarks = null;
		private Boolean reverse = null;
		private Boolean rotateAndCenter = null;
		private Boolean shrinkToFit = null;
		private Boolean trimMarks = null;
		private Boolean useMaxJPEGImageResolution = null;
		private final byte[] inPdfDoc;
		
		public ToPSOptionsSpecParameters(byte[] inPdfDoc) {
			super();
			this.inPdfDoc = inPdfDoc;
		}

		public Color getColor() {
			return color;
		}

		private ToPSOptionsSpecParameters setColor(String color) {
			this.color = Color.valueOf(color);
			return this;
		}

		public FontInclusion getFontInclusion() {
			return fontInclusion;
		}

		private ToPSOptionsSpecParameters setFontInclusion(String fontInclusion) {
			this.fontInclusion = FontInclusion.valueOf(fontInclusion);
			return this;
		}

		public LineWeight getLineWeight() {
			return lineWeight;
		}

		private ToPSOptionsSpecParameters setLineWeight(String lineWeight) {
			this.lineWeight = LineWeight.valueOf(lineWeight);
			return this;
		}

		public String getPageRange() {
			return pageRange;
		}

		private ToPSOptionsSpecParameters setPageRange(String pageRange) {
			this.pageRange = pageRange;
			return this;
		}

		public PageSize getPageSize() {
			return pageSize;
		}

		private ToPSOptionsSpecParameters setPageSize(String pageSize) {
			this.pageSize = PageSize.valueOf(pageSize);
			return this;
		}

		public String getPageSizeHeight() {
			return pageSizeHeight;
		}

		private ToPSOptionsSpecParameters setPageSizeHeight(String pageSizeHeight) {
			this.pageSizeHeight = pageSizeHeight;
			return this;
		}

		public String getPageSizeWidth() {
			return pageSizeWidth;
		}

		private ToPSOptionsSpecParameters setPageSizeWidth(String pageSizeWidth) {
			this.pageSizeWidth = pageSizeWidth;
			return this;
		}

		public PSLevel getPsLevel() {
			return psLevel;
		}

		private ToPSOptionsSpecParameters setPsLevel(String psLevel) {
			this.psLevel = PSLevel.valueOf(psLevel);
			return this;
		}

		public Style getStyle() {
			return style;
		}

		private ToPSOptionsSpecParameters setStyle(String style) {
			this.style = Style.valueOf(style);
			return this;
		}

		public Boolean getAllowBinaryContent() {
			return allowBinaryContent;
		}

		private ToPSOptionsSpecParameters setAllowBinaryContent(String allowBinaryContent) {
			this.allowBinaryContent = Boolean.valueOf(allowBinaryContent);
			return this;
		}

		public Boolean getBleedMarks() {
			return bleedMarks;
		}

		private ToPSOptionsSpecParameters setBleedMarks(String bleedMarks) {
			this.bleedMarks = Boolean.valueOf(bleedMarks);
			return this;
		}

		public Boolean getColorBars() {
			return colorBars;
		}

		private ToPSOptionsSpecParameters setColorBars(String colorBars) {
			this.colorBars = Boolean.valueOf(colorBars);
			return this;
		}

		public Boolean getConvertTrueTypeToType1() {
			return convertTrueTypeToType1;
		}

		private ToPSOptionsSpecParameters setConvertTrueTypeToType1(String convertTrueTypeToType1) {
			this.convertTrueTypeToType1 = Boolean.valueOf(convertTrueTypeToType1);
			return this;
		}

		public Boolean getEmitCIDFontType2() {
			return emitCIDFontType2;
		}

		private ToPSOptionsSpecParameters setEmitCIDFontType2(String emitCIDFontType2) {
			this.emitCIDFontType2 = Boolean.valueOf(emitCIDFontType2);
			return this;
		}

		public Boolean getEmitPSFormObjects() {
			return emitPSFormObjects;
		}

		private ToPSOptionsSpecParameters setEmitPSFormObjects(String emitPSFormObjects) {
			this.emitPSFormObjects = Boolean.valueOf(emitPSFormObjects);
			return this;
		}

		public Boolean getExpandToFit() {
			return expandToFit;
		}

		private ToPSOptionsSpecParameters setExpandToFit(String expandToFit) {
			this.expandToFit = Boolean.valueOf(expandToFit);
			return this;
		}

		public Boolean getIncludeComments() {
			return includeComments;
		}

		private ToPSOptionsSpecParameters setIncludeComments(String includeComments) {
			this.includeComments = Boolean.valueOf(includeComments);
			return this;
		}

		public Boolean getLegacyToSimplePSFlag() {
			return legacyToSimplePSFlag;
		}

		private ToPSOptionsSpecParameters setLegacyToSimplePSFlag(String legacyToSimplePSFlag) {
			this.legacyToSimplePSFlag = Boolean.valueOf(legacyToSimplePSFlag);
			return this;
		}

		public Boolean getPageInformation() {
			return pageInformation;
		}

		private ToPSOptionsSpecParameters setPageInformation(String pageInformation) {
			this.pageInformation = Boolean.valueOf(pageInformation);
			return this;
		}

		public Boolean getRegistrationMarks() {
			return registrationMarks;
		}

		private ToPSOptionsSpecParameters setRegistrationMarks(String registrationMarks) {
			this.registrationMarks = Boolean.valueOf(registrationMarks);
			return this;
		}

		public Boolean getReverse() {
			return reverse;
		}

		private ToPSOptionsSpecParameters setReverse(String reverse) {
			this.reverse = Boolean.valueOf(reverse);
			return this;
		}

		public Boolean getRotateAndCenter() {
			return rotateAndCenter;
		}

		private ToPSOptionsSpecParameters setRotateAndCenter(String rotateAndCenter) {
			this.rotateAndCenter = Boolean.valueOf(rotateAndCenter);
			return this;
		}

		public Boolean getShrinkToFit() {
			return shrinkToFit;
		}

		private ToPSOptionsSpecParameters setShrinkToFit(String shrinkToFit) {
			this.shrinkToFit = Boolean.valueOf(shrinkToFit);
			return this;
		}

		public Boolean getTrimMarks() {
			return trimMarks;
		}

		private ToPSOptionsSpecParameters setTrimMarks(String trimMarks) {
			this.trimMarks = Boolean.valueOf(trimMarks);
			return this;
		}

		public Boolean getUseMaxJPEGImageResolution() {
			return useMaxJPEGImageResolution;
		}

		private ToPSOptionsSpecParameters setUseMaxJPEGImageResolution(String useMaxJPEGImageResolution) {
			this.useMaxJPEGImageResolution = Boolean.valueOf(useMaxJPEGImageResolution);
			return this;
		}

		public byte[] getInPdfDoc() {
			return inPdfDoc;
		}

		public static ToPSOptionsSpecParameters readToPSOptionsSpecParameters(SlingHttpServletRequest request) throws BadRequestException {
			try {
				byte[] inPdfDoc = getMandatoryParameter(request, PDF_PARAM).get();
				
				ToPSOptionsSpecParameters result = new ToPSOptionsSpecParameters(inPdfDoc);		
				
				getOptionalParameter(request, COLOR_PARAM).ifPresent(rp->result.setColor(rp.getString()));
				getOptionalParameter(request, FONT_INCLUSION_PARAM).ifPresent(rp->result.setFontInclusion(rp.getString()));
				getOptionalParameter(request, LINE_WEIGHT_PARAM).ifPresent(rp->result.setLineWeight(rp.getString()));
				getOptionalParameter(request, PAGE_RANGE_PARAM).ifPresent(rp->result.setPageRange(rp.getString()));
				getOptionalParameter(request, PAGE_SIZE_PARAM).ifPresent(rp->result.setPageSize(rp.getString()));
				getOptionalParameter(request, PAGE_SIZE_HEIGHT_PARAM).ifPresent(rp->result.setPageSizeHeight(rp.getString()));
				getOptionalParameter(request, PAGE_SIZE_WIDTH_PARAM).ifPresent(rp->result.setPageSizeWidth(rp.getString()));
				getOptionalParameter(request, PS_LEVEL_PARAM).ifPresent(rp->result.setPsLevel(rp.getString()));
				getOptionalParameter(request, STYLE_PARAM).ifPresent(rp->result.setStyle(rp.getString()));
				getOptionalParameter(request, ALLOW_BINARY_CONTENT_PARAM).ifPresent(rp->result.setAllowBinaryContent(rp.getString()));
				getOptionalParameter(request, BLEED_MARKS_PARAM).ifPresent(rp->result.setBleedMarks(rp.getString()));
				getOptionalParameter(request, COLOR_BARS_PARAM).ifPresent(rp->result.setColorBars(rp.getString()));
				getOptionalParameter(request, CONVERT_TRUE_TYPE_TO_TYPE1_PARAM).ifPresent(rp->result.setConvertTrueTypeToType1(rp.getString()));
				getOptionalParameter(request, EMIT_CID_FONT_TYPE2_PARAM).ifPresent(rp->result.setEmitCIDFontType2(rp.getString()));
				getOptionalParameter(request, EMIT_PS_FORM_OBJECTS_PARAM).ifPresent(rp->result.setEmitPSFormObjects(rp.getString()));
				getOptionalParameter(request, EXPAND_TO_FIT_PARAM).ifPresent(rp->result.setExpandToFit(rp.getString()));
				getOptionalParameter(request, INCLUDE_COMMENTS_PARAM).ifPresent(rp->result.setIncludeComments(rp.getString()));
				getOptionalParameter(request, LEGACY_TO_SIMPLE_PS_FLAG_PARAM).ifPresent(rp->result.setLegacyToSimplePSFlag(rp.getString()));
				getOptionalParameter(request, PAGE_INFORMATION_PARAM).ifPresent(rp->result.setPageInformation(rp.getString()));
				getOptionalParameter(request, REGISTRATION_MARKS_PARAM).ifPresent(rp->result.setRegistrationMarks(rp.getString()));
				getOptionalParameter(request, REVERSE_PARAM).ifPresent(rp->result.setReverse(rp.getString()));
				getOptionalParameter(request, ROTATE_AND_CENTER_PARAM).ifPresent(rp->result.setRotateAndCenter(rp.getString()));
				getOptionalParameter(request, SHRINK_TO_FIT_PARAM).ifPresent(rp->result.setShrinkToFit(rp.getString()));
				getOptionalParameter(request, TRIM_MARKS_PARAM).ifPresent(rp->result.setTrimMarks(rp.getString()));
				getOptionalParameter(request, USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM).ifPresent(rp->result.setUseMaxJPEGImageResolution(rp.getString()));

				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}
	}
}
