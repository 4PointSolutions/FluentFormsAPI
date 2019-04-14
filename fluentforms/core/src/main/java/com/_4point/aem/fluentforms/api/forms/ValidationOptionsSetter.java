package com._4point.aem.fluentforms.api.forms;

import java.nio.file.Path;

public interface ValidationOptionsSetter {

	ValidationOptionsSetter setContentRoot(Path contentRootDir);

	ValidationOptionsSetter setDebugDir(Path debugDir);

}