//package com._4point.aem.fluentforms.impl.forms;
//
//import java.util.List;
//
//import com.adobe.fd.forms.api.AcrobatVersion;
//import com.adobe.fd.forms.api.CacheStrategy;
//import com.adobe.fd.forms.api.PDFFormRenderOptions;
//
//public class PDFFormRenderOptionsBuilder implements PDFFormRenderOptionsSetter {
//
//	PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptions();
//	
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setAcrobatVersion(com.adobe.fd.forms.api.AcrobatVersion)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setAcrobatVersion(AcrobatVersion acrobatVersion) {
//		pdfFormRenderOptions.setAcrobatVersion(acrobatVersion);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setCacheStrategy(com.adobe.fd.forms.api.CacheStrategy)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setCacheStrategy(CacheStrategy strategy) {
//		pdfFormRenderOptions.setCacheStrategy(strategy);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setContentRoot(java.lang.String)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setContentRoot(String url) {
//		pdfFormRenderOptions.setContentRoot(url);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setDebugDir(java.lang.String)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setDebugDir(String debugDir) {
//		pdfFormRenderOptions.setDebugDir(debugDir);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setLocale(java.lang.String)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setLocale(String locale) {
//		pdfFormRenderOptions.setLocale(locale);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setSubmitUrls(java.util.List)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setSubmitUrls(List<String> urls) {
//		pdfFormRenderOptions.setSubmitUrls(urls);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setTaggedPDF(boolean)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setTaggedPDF(boolean isTagged) {
//		pdfFormRenderOptions.setTaggedPDF(isTagged);
//		return this;
//	}
//
//	/* (non-Javadoc)
//	 * @see com._4point.aem.fluentforms.api.impl.PDFFormRenderOptionsSetter#setXci(com.adobe.aemfd.docmanager.Document)
//	 */
//	@Override
//	public PDFFormRenderOptionsBuilder setXci(Document xci) {
//		pdfFormRenderOptions.setXci(xci);
//		return this;
//	}
//
//	public PDFFormRenderOptions build() {
//		return pdfFormRenderOptions;
//	}
//}
