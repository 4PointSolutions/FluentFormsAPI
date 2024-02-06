package com._4point.aem.fluentforms.api.convertPdf;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
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

public interface ConvertPdfService {

	List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException;
	
	ToImageArgumentBuilder toImage();
	
	Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException;
	
	ToPSArgumentBuilder toPS();
	
	@SuppressWarnings("serial")
	public static class ConvertPdfServiceException extends Exception {

		public ConvertPdfServiceException() {
			super();
		}

		public ConvertPdfServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public ConvertPdfServiceException(String message) {
			super(message);
		}

		public ConvertPdfServiceException(Throwable cause) {
			super(cause);
		}
	}
	
	public static interface ToImageArgumentBuilder extends ToImageOptionsSpecSetter, Transformable<ToImageArgumentBuilder> {
	
		@Override
		ToImageArgumentBuilder setCmykPolicy(CMYKPolicy cmykPolicy);
		
		@Override
		ToImageArgumentBuilder setColorCompression(ColorCompression colorCompression);
		
		@Override
		ToImageArgumentBuilder setColorSpace(ColorSpace colorSpace);
		
		@Override
		ToImageArgumentBuilder setFilter(PNGFilter filter);
		
		@Override
		ToImageArgumentBuilder setFormat(JPEGFormat format);
		
		@Override
		ToImageArgumentBuilder setGrayScaleCompression(GrayScaleCompression grayScaleCompression);
		
		@Override
		ToImageArgumentBuilder setGrayScalePolicy(GrayScalePolicy grayScalePolicy);
		
		@Override
		ToImageArgumentBuilder setImageConvertFormat(ImageConvertFormat imageConvertFormat);
		
		@Override
		ToImageArgumentBuilder setImageSizeHeight(String imageSizeHeight);
		
		@Override
		ToImageArgumentBuilder setImageSizeWidth(String imageSizeWidth);
		
		@Override
		ToImageArgumentBuilder setIncludeComments(boolean includeComments);
		
		@Override
		ToImageArgumentBuilder setInterlace(Interlace interlace);
		
		@Override
		ToImageArgumentBuilder setMonochrome(MonochromeCompression monochrome);
		
		@Override
		ToImageArgumentBuilder setMultiPageTiff(Boolean multiPageTiff);
		
		@Override
		ToImageArgumentBuilder setPageRange(String pageRange);
		
		@Override
		ToImageArgumentBuilder setResolution(String resolution);
		
		@Override
		ToImageArgumentBuilder setRgbPolicy(RGBPolicy rgbPolicy);
		
		@Override
		ToImageArgumentBuilder setRowsPerStrip(int rowsPerStrip);
		
		@Override
		ToImageArgumentBuilder setTileSize(int tileSize);
		
		@Override
		ToImageArgumentBuilder setUseLegacyImageSizeBehavior(boolean useLegacyImageSizeBehavior);
		
		public List<Document> executeOn(Document inPdfDoc) throws ConvertPdfServiceException;
		
		default public List<Document> executeOn(byte[] inPdf) throws ConvertPdfServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(inPdf));
		};
	}

	public static interface ToPSArgumentBuilder extends ToPSOptionsSpecSetter, Transformable<ToPSArgumentBuilder> {
	
		@Override
		ToPSArgumentBuilder setAllowedBinaryContent(boolean allowBinaryContent);
		
		@Override
		ToPSArgumentBuilder setBleedMarks(boolean bleedMarks);
		
		@Override
		ToPSArgumentBuilder setColor(Color color);
		
		@Override
		ToPSArgumentBuilder setColorBars(boolean colorBars);
		
		@Override
		ToPSArgumentBuilder setConvertTrueTypeToType1(boolean convertTrueTypeToType1);
		
		@Override
		ToPSArgumentBuilder setEmitCIDFontType2(boolean emitCIDFontType2);
		
		@Override
		ToPSArgumentBuilder setEmitPSFormObjects(boolean emitPSFormObjects);
		
		@Override
		ToPSArgumentBuilder setExpandToFit(boolean expandToFit);
		
		@Override
		ToPSArgumentBuilder setFontInclusion(FontInclusion fontInclusion);
		
		@Override
		ToPSArgumentBuilder setIncludeComments(boolean includeComments);
		
		@Override
		ToPSArgumentBuilder setLegacyToSimplePSFlag(boolean legacyToSimplePSFlag);
		
		@Override
		ToPSArgumentBuilder setLineWeight(LineWeight lineWeight);
		
		@Override
		ToPSArgumentBuilder setPageInformation(boolean pageInformation);
		
		@Override
		ToPSArgumentBuilder setPageRange(String pageRange);
		
		@Override
		ToPSArgumentBuilder setPageSize(PageSize pageSize);
		
		@Override
		ToPSArgumentBuilder setPageSizeHeight(String pageSizeHeight);
		
		@Override
		ToPSArgumentBuilder setPageSizeWidth(String pageSizeWidth);
		
		@Override
		ToPSArgumentBuilder setPsLevel(PSLevel psLevel);
		
		@Override
		ToPSArgumentBuilder setRegistrationMarks(boolean registrationMarks);
		
		@Override
		ToPSArgumentBuilder setReverse(boolean reverse);
		
		@Override
		ToPSArgumentBuilder setRotateAndCenter(boolean rotateAndCenter);
		
		@Override
		ToPSArgumentBuilder setShrinkToFit(boolean shrinkToFit);
		
		@Override
		ToPSArgumentBuilder setStyle(Style style);
		
		@Override
		ToPSArgumentBuilder setTrimMarks(boolean trimMarks);
		
		@Override
		ToPSArgumentBuilder setUseMaxJPEGImageResolution(boolean useMaxJPEGImageResolution);
		
		public Document executeOn(Document inPdfDoc) throws ConvertPdfServiceException;

		default public Document executeOn(byte[] inPdf) throws ConvertPdfServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(inPdf));
		};
	}
}