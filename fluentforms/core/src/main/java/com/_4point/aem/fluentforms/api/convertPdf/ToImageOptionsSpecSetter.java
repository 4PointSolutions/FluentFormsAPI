package com._4point.aem.fluentforms.api.convertPdf;

import com.adobe.fd.cpdf.api.enumeration.CMYKPolicy;
import com.adobe.fd.cpdf.api.enumeration.ColorCompression;
import com.adobe.fd.cpdf.api.enumeration.ColorSpace;
import com.adobe.fd.cpdf.api.enumeration.PNGFilter;
import com.adobe.fd.cpdf.api.enumeration.JPEGFormat;
import com.adobe.fd.cpdf.api.enumeration.GrayScaleCompression;
import com.adobe.fd.cpdf.api.enumeration.GrayScalePolicy;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.Interlace;
import com.adobe.fd.cpdf.api.enumeration.MonochromeCompression;
import com.adobe.fd.cpdf.api.enumeration.RGBPolicy;

public interface ToImageOptionsSpecSetter {
	
	ToImageOptionsSpecSetter setCmykPolicy(CMYKPolicy cmykPolicy);
	
	ToImageOptionsSpecSetter setColorCompression(ColorCompression colorCompression);
	
	ToImageOptionsSpecSetter setColorSpace(ColorSpace colorSpace);
	
	ToImageOptionsSpecSetter setFilter(PNGFilter filter);
	
	ToImageOptionsSpecSetter setFormat(JPEGFormat format);
	
	ToImageOptionsSpecSetter setGrayScaleCompression(GrayScaleCompression grayScaleCompression);
	
	ToImageOptionsSpecSetter setGrayScalePolicy(GrayScalePolicy grayScalePolicy);
	
	ToImageOptionsSpecSetter setImageConvertFormat(ImageConvertFormat imageConvertFormat);
	
	ToImageOptionsSpecSetter setImageSizeHeight(String imageSizeHeight);
	
	ToImageOptionsSpecSetter setImageSizeWidth(String imageSizeWidth);
	
	ToImageOptionsSpecSetter setIncludeComments(boolean includeComments);
	
	ToImageOptionsSpecSetter setInterlace(Interlace interlace);
	
	ToImageOptionsSpecSetter setMonochrome(MonochromeCompression monochrome);
	
	ToImageOptionsSpecSetter setMultiPageTiff(Boolean multiPageTiff);
	
	ToImageOptionsSpecSetter setPageRange(String pageRange);
	
	ToImageOptionsSpecSetter setResolution(String resolution);
	
	ToImageOptionsSpecSetter setRgbPolicy(RGBPolicy rgbPolicy);
	
	ToImageOptionsSpecSetter setRowsPerStrip(int rowsPerStrip);
	
	ToImageOptionsSpecSetter setTileSize(int tileSize);
	
	ToImageOptionsSpecSetter setUseLegacyImageSizeBehavior(boolean useLegacyImageSizeBehavior);
}
