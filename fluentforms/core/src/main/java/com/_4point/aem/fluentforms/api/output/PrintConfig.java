package com._4point.aem.fluentforms.api.output;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.output.PrintConfigImpl;
import com.adobe.fd.output.api.RenderType;

public interface PrintConfig {

	public static final PrintConfig DPL300 = PrintConfigImpl.DPL300;
	public static final PrintConfig DPL406 = PrintConfigImpl.DPL406;
	public static final PrintConfig DPL600 = PrintConfigImpl.DPL600;
	public static final PrintConfig Generic_PS_L3 = PrintConfigImpl.Generic_PS_L3;
	public static final PrintConfig GenericColor_PCL_5c = PrintConfigImpl.GenericColor_PCL_5c;
	public static final PrintConfig HP_PCL_5e = PrintConfigImpl.HP_PCL_5e;
	public static final PrintConfig IPL300 = PrintConfigImpl.IPL300;
	public static final PrintConfig IPL400 = PrintConfigImpl.IPL400;
	public static final PrintConfig PS_PLAIN = PrintConfigImpl.PS_PLAIN;
	public static final PrintConfig TPCL305 = PrintConfigImpl.TPCL305;
	public static final PrintConfig TPCL600 = PrintConfigImpl.TPCL600;
	public static final PrintConfig ZPL300 = PrintConfigImpl.ZPL300;
	public static final PrintConfig ZPL600 = PrintConfigImpl.ZPL600;

	RenderType getRenderType();

	PathOrUrl getXdcUri();

	String getContentType();

	public static PrintConfig custom(PathOrUrl xdcUri, RenderType renderType) {
		return PrintConfigImpl.custom(xdcUri, renderType);
	}

}