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

public interface ToImageOptionsSpec extends ToImageOptionsSpecSetter {

	CMYKPolicy getCmykPolicy();
	
	ColorCompression getColorCompression();
	
	ColorSpace getColorSpace();
	
	PNGFilter getFilter();
	
	JPEGFormat getFormat();
	
	GrayScaleCompression getGrayScaleCompression();
	
	GrayScalePolicy getGrayScalePolicy();
	
	ImageConvertFormat getImageConvertFormat();
	
	String getImageSizeHeight();
	
	String getImageSizeWidth();
	
	Interlace getInterlace();
	
	MonochromeCompression getMonochrome();
	
	Boolean getMultiPageTiff();
	
	String getPageRange();
	
	String getResolution();
	
	RGBPolicy getRgbPolicy();
	
	Integer getRowsPerStrip();
	
	Integer getTileSize();
	
	Boolean isIncludeComments();
	
	Boolean isUseLegacyImageSizeBehavior();
}
