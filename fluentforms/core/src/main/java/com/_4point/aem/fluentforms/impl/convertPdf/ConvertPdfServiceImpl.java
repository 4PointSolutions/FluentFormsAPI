package com._4point.aem.fluentforms.impl.convertPdf;

import java.util.List;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
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

public class ConvertPdfServiceImpl implements ConvertPdfService {

	private final TraditionalConvertPdfService adobeConvertPdfService;
	
	public ConvertPdfServiceImpl(TraditionalConvertPdfService adobeConvertPdfService) {
		super();
		this.adobeConvertPdfService = new SafeConvertPdfServiceAdapterWrapper(adobeConvertPdfService);
	}
	
	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc cannot be null.");
		Objects.requireNonNull(toImageOptionsSpec, "toImageOptionsSpec cannot be null.");
		return adobeConvertPdfService.toImage(inPdfDoc, toImageOptionsSpec);
	}

	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc cannot be null.");
		return adobeConvertPdfService.toPS(inPdfDoc, toPSOptionsSpec);
	}

	@Override
	public ToImageArgumentBuilder toImage() {
		return new ToImageArgumentBuilderImpl();
	}

	@Override
	public ToPSArgumentBuilder toPS() {
		return new ToPSArgumentBuilderImpl();
	}
	
	protected TraditionalConvertPdfService getAdobeConvertPdfService() {
		return adobeConvertPdfService;
	}
	
	private class ToImageArgumentBuilderImpl implements ToImageArgumentBuilder {

		ToImageOptionsSpec toImageOptionsSpec = new ToImageOptionsSpecImpl();
		
		@Override
		public ToImageArgumentBuilder setCmykPolicy(CMYKPolicy cmykPolicy) {
			this.toImageOptionsSpec.setCmykPolicy(cmykPolicy);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setColorCompression(ColorCompression colorCompression) {
			this.toImageOptionsSpec.setColorCompression(colorCompression);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setColorSpace(ColorSpace colorSpace) {
			this.toImageOptionsSpec.setColorSpace(colorSpace);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setFilter(PNGFilter filter) {
			this.toImageOptionsSpec.setFilter(filter);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setFormat(JPEGFormat format) {
			this.toImageOptionsSpec.setFormat(format);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setGrayScaleCompression(GrayScaleCompression grayScaleCompression) {
			this.toImageOptionsSpec.setGrayScaleCompression(grayScaleCompression);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setGrayScalePolicy(GrayScalePolicy grayScalePolicy) {
			this.toImageOptionsSpec.setGrayScalePolicy(grayScalePolicy);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setImageConvertFormat(ImageConvertFormat imageConvertFormat) {
			this.toImageOptionsSpec.setImageConvertFormat(imageConvertFormat);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setImageSizeHeight(String imageSizeHeight) {
			this.toImageOptionsSpec.setImageSizeHeight(imageSizeHeight);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setImageSizeWidth(String imageSizeWidth) {
			this.toImageOptionsSpec.setImageSizeWidth(imageSizeWidth);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setIncludeComments(boolean includeComments) {
			this.toImageOptionsSpec.setIncludeComments(includeComments);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setInterlace(Interlace interlace) {
			this.toImageOptionsSpec.setInterlace(interlace);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setMonochrome(MonochromeCompression monochrome) {
			this.toImageOptionsSpec.setMonochrome(monochrome);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setMultiPageTiff(Boolean multiPageTiff) {
			this.toImageOptionsSpec.setMultiPageTiff(multiPageTiff);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setPageRange(String pageRange) {
			this.toImageOptionsSpec.setPageRange(pageRange);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setResolution(String resolution) {
			this.toImageOptionsSpec.setResolution(resolution);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setRgbPolicy(RGBPolicy rgbPolicy) {
			this.toImageOptionsSpec.setRgbPolicy(rgbPolicy);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setRowsPerStrip(int rowsPerStrip) {
			this.toImageOptionsSpec.setRowsPerStrip(rowsPerStrip);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setTileSize(int tileSize) {
			this.toImageOptionsSpec.setTileSize(tileSize);
			return this;
		}

		@Override
		public ToImageArgumentBuilder setUseLegacyImageSizeBehavior(boolean useLegacyImageSizeBehavior) {
			this.toImageOptionsSpec.setUseLegacyImageSizeBehavior(useLegacyImageSizeBehavior);
			return this;
		}

		@Override
		public List<Document> executeOn(Document inPdfDoc) throws ConvertPdfServiceException {
			return toImage(inPdfDoc, this.toImageOptionsSpec);
		}
	}
	
	private class ToPSArgumentBuilderImpl implements ToPSArgumentBuilder {

		ToPSOptionsSpec toPSOptionsSpec = new ToPSOptionsSpecImpl();

		@Override
		public ToPSArgumentBuilder setAllowedBinaryContent(boolean allowBinaryContent) {
			this.toPSOptionsSpec.setAllowedBinaryContent(allowBinaryContent);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setBleedMarks(boolean bleedMarks) {
			this.toPSOptionsSpec.setBleedMarks(bleedMarks);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setColor(Color color) {
			this.toPSOptionsSpec.setColor(color);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setColorBars(boolean colorBars) {
			this.toPSOptionsSpec.setColorBars(colorBars);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setConvertTrueTypeToType1(boolean convertTrueTypeToType1) {
			this.toPSOptionsSpec.setConvertTrueTypeToType1(convertTrueTypeToType1);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setEmitCIDFontType2(boolean emitCIDFontType2) {
			this.toPSOptionsSpec.setEmitCIDFontType2(emitCIDFontType2);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setEmitPSFormObjects(boolean emitPSFormObjects) {
			this.toPSOptionsSpec.setEmitPSFormObjects(emitPSFormObjects);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setExpandToFit(boolean expandToFit) {
			this.toPSOptionsSpec.setExpandToFit(expandToFit);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setFontInclusion(FontInclusion fontInclusion) {
			this.toPSOptionsSpec.setFontInclusion(fontInclusion);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setIncludeComments(boolean includeComments) {
			this.toPSOptionsSpec.setIncludeComments(includeComments);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setLegacyToSimplePSFlag(boolean legacyToSimplePSFlag) {
			this.toPSOptionsSpec.setLegacyToSimplePSFlag(legacyToSimplePSFlag);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setLineWeight(LineWeight lineWeight) {
			this.toPSOptionsSpec.setLineWeight(lineWeight);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPageInformation(boolean pageInformation) {
			this.toPSOptionsSpec.setPageInformation(pageInformation);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPageRange(String pageRange) {
			this.toPSOptionsSpec.setPageRange(pageRange);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPageSize(PageSize pageSize) {
			this.toPSOptionsSpec.setPageSize(pageSize);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPageSizeHeight(String pageSizeHeight) {
			this.toPSOptionsSpec.setPageSizeHeight(pageSizeHeight);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPageSizeWidth(String pageSizeWidth) {
			this.toPSOptionsSpec.setPageSizeWidth(pageSizeWidth);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setPsLevel(PSLevel psLevel) {
			this.toPSOptionsSpec.setPsLevel(psLevel);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setRegistrationMarks(boolean registrationMarks) {
			this.toPSOptionsSpec.setRegistrationMarks(registrationMarks);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setReverse(boolean reverse) {
			this.toPSOptionsSpec.setReverse(reverse);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setRotateAndCenter(boolean rotateAndCenter) {
			this.toPSOptionsSpec.setRotateAndCenter(rotateAndCenter);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setShrinkToFit(boolean shrinkToFit) {
			this.toPSOptionsSpec.setShrinkToFit(shrinkToFit);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setStyle(Style style) {
			this.toPSOptionsSpec.setStyle(style);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setTrimMarks(boolean trimMarks) {
			this.toPSOptionsSpec.setTrimMarks(trimMarks);
			return this;
		}

		@Override
		public ToPSArgumentBuilder setUseMaxJPEGImageResolution(boolean useMaxJPEGImageResolution) {
			this.toPSOptionsSpec.setUseMaxJPEGImageResolution(useMaxJPEGImageResolution);
			return this;
		}

		@Override
		public Document executeOn(Document inPdfDoc) throws ConvertPdfServiceException {
			return toPS(inPdfDoc, this.toPSOptionsSpec);
		}
	}

}
