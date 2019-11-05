package com._4point.aem.fluentforms.impl.output;

import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com.adobe.fd.output.api.AcrobatVersion;

public class PDFOutputOptionsImpl implements PDFOutputOptions {

	private AcrobatVersion acrobatVersion;
	private PathOrUrl contentRoot;
	private Path debugDir;
	private Boolean embedFonts;
	private Boolean linearizedPDF;
	private Locale locale;
	private Boolean retainPDFFormState;
	private Boolean retainUnsignedSignatureFields;
	private Boolean taggedPDF;
	private Document xci;

	@Override
	public AcrobatVersion getAcrobatVersion() {
		return acrobatVersion;
	}

	@Override
	public PathOrUrl getContentRoot() {
		return contentRoot;
	}

	@Override
	public Path getDebugDir() {
		return debugDir;
	}

	@Override
	public Boolean getEmbedFonts() {
		return embedFonts;
	}

	@Override
	public Boolean getLinearizedPDF() {
		return linearizedPDF;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public Boolean getRetainPDFFormState() {
		return retainPDFFormState;
	}

	@Override
	public Boolean getRetainUnsignedSignatureFields() {
		return retainUnsignedSignatureFields;
	}

	@Override
	public Boolean getTaggedPDF() {
		return taggedPDF;
	}

	@Override
	public Document getXci() {
		return xci;
	}

	@Override
	public PDFOutputOptionsImpl setAcrobatVersion(AcrobatVersion acrobatVersion) {
		this.acrobatVersion = acrobatVersion;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setContentRoot(PathOrUrl contentRoot) {
		this.contentRoot = contentRoot;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setDebugDir(Path debugDir) {
		this.debugDir = debugDir;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setEmbedFonts(boolean embedFonts) {
		this.embedFonts = embedFonts;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setLinearizedPDF(boolean linearizedPDF) {
		this.linearizedPDF = linearizedPDF;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setLocale(Locale locale) {
		this.locale = locale;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setRetainPDFFormState(boolean retainFormState) {
		this.retainPDFFormState = retainFormState;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields) {
		this.retainUnsignedSignatureFields = retainUnsignedSignatureFields;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setTaggedPDF(boolean isTagged) {
		this.taggedPDF = isTagged;
		return this;
	}

	@Override
	public PDFOutputOptionsImpl setXci(Document xci) {
		this.xci = xci;;
		return this;
	}

}
