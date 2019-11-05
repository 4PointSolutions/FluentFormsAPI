package com._4point.aem.fluentforms.impl.output;

import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com.adobe.fd.output.api.PaginationOverride;

public class PrintedOutputOptionsImpl implements PrintedOutputOptions  {
	
	private PathOrUrl contentRoot;
	private Integer copies;
	private Path debugDir;
	private Locale locale;
	private PaginationOverride paginationOverride;
	private PrintConfig printConfig;
	private Document xci;

	@Override
	public PathOrUrl getContentRoot() {
		return contentRoot;
	}

	@Override
	public Integer getCopies() {
		return copies;
	}

	@Override
	public Path getDebugDir() {
		return debugDir;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public PaginationOverride getPaginationOverride() {
		return paginationOverride;
	}

	@Override
	public PrintConfig getPrintConfig() {
		return printConfig;
	}

	@Override
	public Document getXci() {
		return xci;
	}

	@Override
	public PrintedOutputOptionsImpl setContentRoot(PathOrUrl url) {
		this.contentRoot = url;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setCopies(int copies) {
		this.copies = copies;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setDebugDir(Path debugDir) {
		this.debugDir = debugDir;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setLocale(Locale locale) {
		this.locale = locale;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setPaginationOverride(PaginationOverride paginationOverride) {
		this.paginationOverride  = paginationOverride;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setPrintConfig(PrintConfig printConfig) {
		this.printConfig = printConfig;
		return this;
	}

	@Override
	public PrintedOutputOptionsImpl setXci(Document xci) {
		this.xci = xci;
		return this;
	}

}
