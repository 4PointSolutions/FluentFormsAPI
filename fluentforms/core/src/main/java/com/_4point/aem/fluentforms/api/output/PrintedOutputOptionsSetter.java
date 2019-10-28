package com._4point.aem.fluentforms.api.output;

import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.output.api.PaginationOverride;

public interface PrintedOutputOptionsSetter {

	PrintedOutputOptions setContentRoot(PathOrUrl pathOrUrl);

	default PrintedOutputOptions setContentRoot(Path path) {
		return setContentRoot(new PathOrUrl(path));
	}

	default PrintedOutputOptions setContentRoot(URL url) {
		return setContentRoot(new PathOrUrl(url));
	}

	PrintedOutputOptions setCopies(int copies);

	PrintedOutputOptions setDebugDir(Path debugDir);

	PrintedOutputOptions setLocale(Locale locale);

	PrintedOutputOptions setPaginationOverride(PaginationOverride paginationOverride);

	PrintedOutputOptions setPrintConfig(PrintConfig printConfig);

	PrintedOutputOptions setXci(Document xci);

}