package com._4point.aem.fluentforms.api.output;

import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Xci;
import com.adobe.fd.output.api.PaginationOverride;

public interface PrintedOutputOptionsSetter extends PrintedOutputOptionsSetter2 {

	@Override
	PrintedOutputOptionsSetter setContentRoot(PathOrUrl pathOrUrl);

	@Override
	default PrintedOutputOptionsSetter setContentRoot(Path path) {
		return setContentRoot(PathOrUrl.from(path));
	}

	@Override
	default PrintedOutputOptionsSetter setContentRoot(URL url) {
		return setContentRoot(PathOrUrl.from(url));
	}

	@Override
	PrintedOutputOptionsSetter setCopies(int copies);

	@Override
	PrintedOutputOptionsSetter setDebugDir(Path debugDir);

	@Override
	PrintedOutputOptionsSetter setLocale(Locale locale);

	@Override
	PrintedOutputOptionsSetter setPaginationOverride(PaginationOverride paginationOverride);

	PrintedOutputOptionsSetter setPrintConfig(PrintConfig printConfig);

	@Override
	PrintedOutputOptionsSetter setXci(Document xci);

	@Override
	default PrintedOutputOptionsSetter setXci(Xci xci) {
		return setXci(xci.toDocument());
	}
}