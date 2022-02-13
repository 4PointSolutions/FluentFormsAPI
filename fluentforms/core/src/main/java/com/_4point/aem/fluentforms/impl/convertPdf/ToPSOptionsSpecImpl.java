package com._4point.aem.fluentforms.impl.convertPdf;

import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpecSetter;
import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.Style;

public class ToPSOptionsSpecImpl implements ToPSOptionsSpec {

	private Color color;
	private FontInclusion fontInclusion;
	private LineWeight lineWeight;
	private String pageRange;
	private PageSize pageSize;
	private String pageSizeHeight;
	private String pageSizeWidth;
	private PSLevel psLevel;
	private Style style;
	private Boolean allowBinaryContent;
	private Boolean bleedMarks;
	private Boolean colorBars;
	private Boolean convertTrueTypeToType1;
	private Boolean emitCIDFontType2;
	private Boolean emitPSFormObjects;
	private Boolean expandToFit;
	private Boolean includeComments;
	private Boolean legacyToSimplePSFlag;
	private Boolean pageInformation;
	private Boolean registrationMarks;
	private Boolean reverse;
	private Boolean rotateAndCenter;
	private Boolean shrinkToFit;
	private Boolean trimMarks;
	private Boolean useMaxJPEGImageResolution;
		
	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public FontInclusion getFontInclusion() {
		return fontInclusion;
	}
	
	@Override
	public LineWeight getLineWeight() {
		return lineWeight;
	}
	
	@Override
	public String getPageRange() {
		return pageRange;
	}
	
	@Override
	public PageSize getPageSize() {
		return pageSize;
	}
	
	@Override
	public String getPageSizeHeight() {
		return pageSizeHeight;
	}
	
	@Override
	public String getPageSizeWidth() {
		return pageSizeWidth;
	}
	
	@Override
	public PSLevel getPsLevel() {
		return psLevel;
	}
	
	@Override
	public Style getStyle() {
		return style;
	}
	
	@Override
	public Boolean isAllowBinaryContent() {
		return allowBinaryContent;
	}
	
	@Override
	public Boolean isBleedMarks() {
		return bleedMarks;
	}
	
	@Override
	public Boolean isColorBars() {
		return colorBars;
	}
	
	@Override
	public Boolean isConvertTrueTypeToType1() {
		return convertTrueTypeToType1;
	}
	
	@Override
	public Boolean isEmitCIDFontType2() {
		return emitCIDFontType2;
	}
	
	@Override
	public Boolean isEmitPSFormObjects() {
		return emitPSFormObjects;
	}
	
	@Override
	public Boolean isExpandToFit() {
		return expandToFit;
	}
	
	@Override
	public Boolean isIncludeComments() {
		return includeComments;
	}
	
	@Override
	public Boolean isLegacyToSimplePSFlag() {
		return legacyToSimplePSFlag;
	}
	
	@Override
	public Boolean isPageInformation() {
		return pageInformation;
	}
	
	@Override
	public Boolean isRegistrationMarks() {
		return registrationMarks;
	}
	
	@Override
	public Boolean isReverse() {
		return reverse;
	}
	
	@Override
	public Boolean isRotateAndCenter() {
		return rotateAndCenter;
	}
	
	@Override
	public Boolean isShrinkToFit() {
		return shrinkToFit;
	}
	
	@Override
	public Boolean isTrimMarks() {
		return trimMarks;
	}
	
	@Override
	public Boolean isUseMaxJPEGImageResolution() {
		return useMaxJPEGImageResolution;
	}
	
	@Override
	public ToPSOptionsSpecSetter setAllowedBinaryContent(boolean allowBinaryContent) {
		this.allowBinaryContent = allowBinaryContent;
		return this;
	}
	
	
	@Override
	public ToPSOptionsSpecSetter setBleedMarks(boolean bleedMarks) {
		this.bleedMarks = bleedMarks;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setColor(Color color) {
		this.color = color;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setColorBars(boolean colorBars) {
		this.colorBars = colorBars;	
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setConvertTrueTypeToType1(boolean convertTrueTypeToType1) {
		this.convertTrueTypeToType1 = convertTrueTypeToType1;
		return this;
	}
		
	@Override
	public ToPSOptionsSpecSetter setEmitCIDFontType2(boolean emitCIDFontType2) {
		this.emitCIDFontType2 = emitCIDFontType2;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setEmitPSFormObjects(boolean emitPSFormObjects) {
		this.emitPSFormObjects = emitPSFormObjects;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setExpandToFit(boolean expandToFit) {
		this.expandToFit = expandToFit;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setFontInclusion(FontInclusion fontInclusion) {
		this.fontInclusion = fontInclusion;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setIncludeComments(boolean includeComments) {
		this.includeComments = includeComments;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setLegacyToSimplePSFlag(boolean legacyToSimplePSFlag) {
		this.legacyToSimplePSFlag = legacyToSimplePSFlag;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setLineWeight(LineWeight lineWeight) {
		this.lineWeight = lineWeight;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPageInformation(boolean pageInformation) {
		this.pageInformation = pageInformation;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPageRange(String pageRange) {
		this.pageRange = pageRange;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPageSize(PageSize pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPageSizeHeight(String pageSizeHeight) {
		this.pageSizeHeight = pageSizeHeight;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPageSizeWidth(String pageSizeWidth) {
		this.pageSizeWidth = pageSizeWidth;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setPsLevel(PSLevel psLevel) {
		this.psLevel = psLevel;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setRegistrationMarks(boolean registrationMarks) {
		this.registrationMarks = registrationMarks;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setReverse(boolean reverse) {
		this.reverse = reverse;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setRotateAndCenter(boolean rotateAndCenter) {
		this.rotateAndCenter = rotateAndCenter;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setShrinkToFit(boolean shrinkToFit) {
		this.shrinkToFit = shrinkToFit;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setStyle(Style style) {
		this.style = style;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setTrimMarks(boolean trimMarks) {
		this.trimMarks = trimMarks;
		return this;
	}
	
	@Override
	public ToPSOptionsSpecSetter setUseMaxJPEGImageResolution(boolean useMaxJPEGImageResolution) {
		this.useMaxJPEGImageResolution = useMaxJPEGImageResolution;
		return this;
	}
}
