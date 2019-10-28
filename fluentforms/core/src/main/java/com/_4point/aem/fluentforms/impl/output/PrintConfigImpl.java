package com._4point.aem.fluentforms.impl.output;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com.adobe.fd.output.api.RenderType;

public class PrintConfigImpl implements PrintConfig {

	public static final PrintConfigImpl DPL300 = new PrintConfigImpl("DPL300", RenderType.DPL);
	public static final PrintConfigImpl DPL406 = new PrintConfigImpl("DPL406", RenderType.DPL);
	public static final PrintConfigImpl DPL600 = new PrintConfigImpl("DPL600", RenderType.DPL);
	public static final PrintConfigImpl Generic_PS_L3 = new PrintConfigImpl("Generic_PS_L3", RenderType.PostScript);
	public static final PrintConfigImpl GenericColor_PCL_5c = new PrintConfigImpl("GenericColor_PCL_5c", RenderType.PCL);
	public static final PrintConfigImpl HP_PCL_5e = new PrintConfigImpl("HP_PCL_5e", RenderType.PCL);
	public static final PrintConfigImpl IPL300 = new PrintConfigImpl("IPL300", RenderType.IPL);
	public static final PrintConfigImpl IPL400 = new PrintConfigImpl("IPL400", RenderType.IPL);
	public static final PrintConfigImpl PS_PLAIN = new PrintConfigImpl("PS_PLAIN", RenderType.PostScript);
	public static final PrintConfigImpl TPCL305 = new PrintConfigImpl("TPCL305", RenderType.TPCL);
	public static final PrintConfigImpl TPCL600 = new PrintConfigImpl("TPCL600", RenderType.TPCL);
	public static final PrintConfigImpl ZPL300 = new PrintConfigImpl("ZPL300", RenderType.ZPL);
	public static final PrintConfigImpl ZPL600 = new PrintConfigImpl("ZPL600", RenderType.ZPL);


	private final RenderType renderType;
	private final PathOrUrl xdc;

	protected PrintConfigImpl(PathOrUrl xdcUri, RenderType renderType) {
		this.xdc = xdcUri;
		this.renderType = renderType;
	}

	protected PrintConfigImpl(String xdcUri, RenderType renderType) {
		this.xdc = PathOrUrl.fromString(xdcUri);
		this.renderType = renderType;
	}

	@Override
	public RenderType getRenderType() {
		return renderType;
	}

	@Override
	public PathOrUrl getXdcUri() {
		return xdc;
	}

	public static PrintConfigImpl custom(PathOrUrl xdcUri, RenderType renderType) {
		return new PrintConfigImpl(xdcUri, renderType);
	}
}
