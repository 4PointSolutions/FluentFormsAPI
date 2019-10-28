package com._4point.aem.fluentforms.api.output;

import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.output.api.PaginationOverride;

public interface PrintedOutputOptions extends PrintedOutputOptionsSetter {

	PathOrUrl getContentRoot();

	int getCopies();

	Path getDebugDir();

	Locale getLocale();

	PaginationOverride getPaginationOverride();

	PrintConfig getPrintConfig();

	Document getXci();

}