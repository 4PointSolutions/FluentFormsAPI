package com._4point.aem.fluentforms.api.assembler;

import java.net.URL;
import java.nio.file.Path;

import com._4point.aem.fluentforms.api.PathOrUrl;

public interface AssemblerOptionsSetter {
	AssemblerOptionsSetter setFailOnError (boolean isFailOnError);

	AssemblerOptionsSetter setContentRoot(PathOrUrl contentRoot);
	
	default AssemblerOptionsSetter setContentRoot(Path contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

	default AssemblerOptionsSetter setContentRoot(URL contentRoot) {
		return setContentRoot(new PathOrUrl(contentRoot));
	}

}
