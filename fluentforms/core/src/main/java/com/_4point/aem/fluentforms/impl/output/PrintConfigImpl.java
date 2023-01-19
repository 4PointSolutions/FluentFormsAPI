package com._4point.aem.fluentforms.impl.output;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com.adobe.fd.output.api.RenderType;

public enum PrintConfigImpl implements PrintConfig {

	DPL300("DPL300", RenderType.DPL),
	DPL406("DPL406", RenderType.DPL),
	DPL600("DPL600", RenderType.DPL),
	Generic_PS_L3("Generic_PS_L3", RenderType.PostScript),
	GenericColor_PCL_5c("GenericColor_PCL_5c", RenderType.PCL),
	HP_PCL_5e("HP_PCL_5e", RenderType.PCL),
	IPL300("IPL300", RenderType.IPL),
	IPL400("IPL400", RenderType.IPL),
	PS_PLAIN("PS_PLAIN", RenderType.PostScript),
	TPCL305("TPCL305", RenderType.TPCL),
	TPCL600("TPCL600", RenderType.TPCL),
	ZPL300("ZPL300", RenderType.ZPL),
	ZPL600("ZPL600", RenderType.ZPL)
	;

	private final RenderType renderType;
	private final PathOrUrl xdc;

	private PrintConfigImpl(PathOrUrl xdcUri, RenderType renderType) {
		this.xdc = xdcUri;
		this.renderType = renderType;
	}

	private PrintConfigImpl(String xdcUri, RenderType renderType) {
		this(PathOrUrl.from(xdcUri), renderType);
	}

	@Override
	public RenderType getRenderType() {
		return renderType;
	}

	@Override
	public PathOrUrl getXdcUri() {
		return xdc;
	}
	
	@Override
	public String getContentType() {
		return determineContentType(this.renderType);
	}

	private static String determineContentType(RenderType renderType) {
		switch(renderType) {
		case DPL:
			return Document.CONTENT_TYPE_DPL;
		case IPL:
			return Document.CONTENT_TYPE_IPL;
		case PCL:
			return Document.CONTENT_TYPE_PCL;
		case PostScript:
			return Document.CONTENT_TYPE_PS;
		case TPCL:
			return Document.CONTENT_TYPE_TPCL;
		case ZPL:
			return Document.CONTENT_TYPE_ZPL;
		default:
			return "application/octet-stream";	
		}
	}

	public static PrintConfig custom(PathOrUrl xdcUri, RenderType renderType) {
		return new PrintConfig() {
			@Override
			public RenderType getRenderType() {
				return renderType;
			}

			@Override
			public PathOrUrl getXdcUri() {
				return xdcUri;
			}

			@Override
			public String getContentType() {
				return determineContentType(getRenderType());
			}};
	}
}
