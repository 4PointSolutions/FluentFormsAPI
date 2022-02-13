package com._4point.aem.fluentforms.impl.convertPdf;

import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpecSetter;
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

public class ToImageOptionsSpecImpl implements ToImageOptionsSpec {

	private CMYKPolicy cmykPolicy;
	private ColorCompression colorCompression;
	private ColorSpace colorSpace;
	private PNGFilter filter;
	private JPEGFormat format;
	private GrayScaleCompression grayScaleCompression;
	private GrayScalePolicy grayScalePolicy;
	private ImageConvertFormat imageConvertFormat;
	private String imageSizeHeight;
	private String imageSizeWidth;
	private Interlace interlace;
	private MonochromeCompression monochrome;
	private Boolean multiPageTiff;
	private String pageRange;
	private String resolution;
	private RGBPolicy rgbPolicy;
	private Integer rowsPerStrip;
	private Integer tileSize;
	private Boolean includeComments;
	private Boolean useLegacyImageSizeBehavior;
	
	@Override
	public CMYKPolicy getCmykPolicy() {
		return cmykPolicy;
	}
	
	@Override
	public ColorCompression getColorCompression() {
		return colorCompression;
	}
	
	@Override
	public ColorSpace getColorSpace() {
		return colorSpace;
	}
	
	@Override
	public PNGFilter getFilter() {
		return filter;
	}
	
	@Override
	public JPEGFormat getFormat() {
		return format;
	}
	
	@Override
	public GrayScaleCompression getGrayScaleCompression() {
		return grayScaleCompression;
	}
	
	@Override
	public GrayScalePolicy getGrayScalePolicy() {
		return grayScalePolicy;
	}
	
	@Override
	public ImageConvertFormat getImageConvertFormat() {
		return imageConvertFormat;
	}
	
	@Override
	public String getImageSizeHeight() {
		return imageSizeHeight;
	}
	
	@Override
	public String getImageSizeWidth() {
		return imageSizeWidth;
	}
	
	@Override
	public Interlace getInterlace() {
		return interlace;
	}
	
	@Override
	public MonochromeCompression getMonochrome() {
		return monochrome;
	}
	
	@Override
	public Boolean getMultiPageTiff() {
		return multiPageTiff;
	}
	
	@Override
	public String getPageRange() {
		return pageRange;
	}
	
	@Override
	public String getResolution() {
		return resolution;
	}
	
	@Override
	public RGBPolicy getRgbPolicy() {
		return rgbPolicy;
	}
	
	@Override
	public Integer getRowsPerStrip() {
		return rowsPerStrip;
	}
	
	@Override
	public Integer getTileSize() {
		return tileSize;
	}
	
	@Override
	public Boolean isIncludeComments() {
		return includeComments;
	}
	
	@Override
	public Boolean isUseLegacyImageSizeBehavior() {
		return useLegacyImageSizeBehavior;
	}
	
	@Override
	public ToImageOptionsSpecSetter setCmykPolicy(CMYKPolicy cmykPolicy) {
		this.cmykPolicy = cmykPolicy;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setColorCompression(ColorCompression colorCompression) {
		this.colorCompression = colorCompression;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setColorSpace(ColorSpace colorSpace) {
		this.colorSpace = colorSpace;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setFilter(PNGFilter filter) {
		this.filter = filter;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setFormat(JPEGFormat format) {
		this.format = format;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setGrayScaleCompression(GrayScaleCompression grayScaleCompression) {
		this.grayScaleCompression = grayScaleCompression;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setGrayScalePolicy(GrayScalePolicy grayScalePolicy) {
		this.grayScalePolicy = grayScalePolicy;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setImageConvertFormat(ImageConvertFormat imageConvertFormat) {
		this.imageConvertFormat = imageConvertFormat;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setImageSizeHeight(String imageSizeHeight) {
		this.imageSizeHeight = imageSizeHeight;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setImageSizeWidth(String imageSizeWidth) {
		this.imageSizeWidth = imageSizeWidth;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setIncludeComments(boolean includeComments) {
		this.includeComments = includeComments;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setInterlace(Interlace interlace) {
		this.interlace = interlace;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setMonochrome(MonochromeCompression monochrome) {
		this.monochrome = monochrome;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setMultiPageTiff(Boolean multiPageTiff) {
		this.multiPageTiff = multiPageTiff;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setPageRange(String pageRange) {
		this.pageRange = pageRange;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setRgbPolicy(RGBPolicy rgbPolicy) {
		this.rgbPolicy = rgbPolicy;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setRowsPerStrip(int rowsPerStrip) {
		this.rowsPerStrip = rowsPerStrip;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setTileSize(int tileSize) {
		this.tileSize = tileSize;
		return this;
	}
	
	@Override
	public ToImageOptionsSpecSetter setUseLegacyImageSizeBehavior(boolean useLegacyImageSizeBehavior) {
		this.useLegacyImageSizeBehavior = useLegacyImageSizeBehavior;
		return this;
	}
}
