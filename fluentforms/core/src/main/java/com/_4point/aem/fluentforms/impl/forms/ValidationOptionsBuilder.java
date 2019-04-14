package com._4point.aem.fluentforms.impl.forms;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptionsSetter;

public class ValidationOptionsBuilder implements ValidationOptionsSetter  {

	private Path contentRootDir;
	private Path debugDir = null;
	
	public ValidationOptionsBuilder() {
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationOptionsSetter#setContentRoot(java.nio.file.Path)
	 */
	@Override
	public ValidationOptionsSetter setContentRoot(Path contentRootDir) {
		this.contentRootDir = contentRootDir;
		return this;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationOptionsSetter#setDebugDir(java.nio.file.Path)
	 */
	@Override
	public ValidationOptionsSetter setDebugDir(Path debugDir) {
		this.debugDir = debugDir;
		return this;
	}

	public ValidationOptions build() throws FileNotFoundException {
		return new ValidationOptionsImpl(contentRootDir, debugDir);
	}
	
}
