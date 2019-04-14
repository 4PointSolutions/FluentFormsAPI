package com._4point.aem.fluentforms.impl.forms;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import com._4point.aem.fluentforms.api.forms.ValidationOptions;


public class ValidationOptionsImpl implements ValidationOptions {
	
	private final Path contentRoot;
	private final Path debugDir;
	
	public ValidationOptionsImpl() {
		this.contentRoot = null;
		this.debugDir = null;
	}

	public ValidationOptionsImpl(Path contentRoot, Path debugDir) throws FileNotFoundException {
		super();
		if (contentRoot != null && !(Files.exists(contentRoot) && Files.isDirectory(contentRoot))) {
			throw new FileNotFoundException("Unable to find content root directory (" + contentRoot + ").");
		}
		this.contentRoot = contentRoot;
		if (debugDir != null && !(Files.exists(debugDir)  && Files.isDirectory(debugDir))) {
			throw new FileNotFoundException("Unable to find debug dumps directory (" + debugDir + ").");
		}
		this.debugDir = debugDir;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationOptions#getContentRoot()
	 */
	@Override
	public Path getContentRoot() {
		return contentRoot;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationOptions#getDebugDir()
	 */
	@Override
	public Path getDebugDir() {
		return debugDir;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.impl.ValidationOptions#toAdobeValidationOptions()
	 */
	@Override
	public com.adobe.fd.forms.api.ValidationOptions toAdobeValidationOptions() {
		com.adobe.fd.forms.api.ValidationOptions options = new com.adobe.fd.forms.api.ValidationOptions();
		setIfNotNull(options::setContentRoot, this.contentRoot.toString());
		setIfNotNull(options::setDebugDir, this.debugDir.toString());
		return options;
	}
}
