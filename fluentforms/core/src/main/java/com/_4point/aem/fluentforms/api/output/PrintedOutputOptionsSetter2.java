package com._4point.aem.fluentforms.api.output;

import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Xci;
import com.adobe.fd.output.api.PaginationOverride;

public interface PrintedOutputOptionsSetter2 {

	PrintedOutputOptionsSetter2 setContentRoot(PathOrUrl pathOrUrl);

	default PrintedOutputOptionsSetter2 setContentRoot(Path path) {
		return setContentRoot(PathOrUrl.from(path));
	}

	default PrintedOutputOptionsSetter2 setContentRoot(URL url) {
		return setContentRoot(PathOrUrl.from(url));
	}

	PrintedOutputOptionsSetter2 setCopies(int copies);

	PrintedOutputOptionsSetter2 setDebugDir(Path debugDir);

	PrintedOutputOptionsSetter2 setLocale(Locale locale);

	PrintedOutputOptionsSetter2 setPaginationOverride(PaginationOverride paginationOverride);

	PrintedOutputOptionsSetter2 setXci(Document xci);

	default PrintedOutputOptionsSetter2 setXci(Xci xci) {
		return setXci(xci.toDocument());
	}
}