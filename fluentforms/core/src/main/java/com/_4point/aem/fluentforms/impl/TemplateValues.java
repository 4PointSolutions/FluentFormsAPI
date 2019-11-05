package com._4point.aem.fluentforms.impl;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import com._4point.aem.fluentforms.api.PathOrUrl;

/**
 * This class is used by the Forms and Output services to normalize the contentRoot and Template locations.
 * 
 * Because all fragments in a template are relative to that template in Designer but relative to the content root
 * when rendering.  We need to make sure the content root points to the directory where the template resides so 
 * that fragments are found.
 *
 */
public class TemplateValues {
	private final PathOrUrl contentRoot;
	private final Path template;
	
	private TemplateValues(PathOrUrl contentRoot, Path template) {
		super();
		this.contentRoot = contentRoot;
		this.template = template;
	}

	// Move any parent on the template to the provided content root (i.e. templates dir).  This is because all fragments in a template
	// are relative to that template in Designer but relative to the content root when rendering.  We need to make sure the content root
	// points to the directory where the template resides so that fragments are found.
	public static TemplateValues determineTemplateValues(Path template, PathOrUrl templatesDir, UsageContext usageContext) throws FileNotFoundException {
		Path templateParentDir = template.getParent();
		PathOrUrl contentRoot;
		if (templatesDir == null && templateParentDir != null) {
			// No templatesDir but there's a parent dir on the template
			// use the parent dir as the content root
			contentRoot = new PathOrUrl(templateParentDir);
		} else if (templatesDir != null && templateParentDir == null) {
			// There's a templatesDir but no parent dir on the template
			// so just use the templatesDir as the content root
			contentRoot = templatesDir;
		} else if (templatesDir != null && templateParentDir != null) {
			// There's a templatesDir and there's a parent dir on the template
			// append the parent dir onto the templates dir to create the content root
			// unless the parent dir is an absolute path.
			if (templateParentDir.isAbsolute()) {
				contentRoot = new PathOrUrl(templateParentDir);
			} else if (templatesDir.isPath()) {
				contentRoot = new PathOrUrl(templatesDir.getPath().resolve(templateParentDir));
			} else if (templatesDir.isUrl()) {
				contentRoot = PathOrUrl.fromString(templatesDir.getUrl().toString() + "/" + templateParentDir.toString());
			} else if (templatesDir.isCrxUrl()) {
				contentRoot = PathOrUrl.fromString(templatesDir.getCrxUrl() + "/" + templateParentDir.toString());
			} else {
				// This should never happen
				throw new IllegalStateException("Context Root is not a Path, Url, or CrxUrl.  This should never happen.");
			}
		} else {	// templatesDir == null && templateParentDir == null
			// No templatesDir and no parent dir on the template, so no content root
			contentRoot = null;
		}
		Path templateFilenamePath = template.getFileName();
		if (contentRoot != null && contentRoot.isPath()) {
			// Since both the content Root and the template are paths, we can check to make sure the file exists.
			Path formsPath = contentRoot != null ? contentRoot.getPath().resolve(templateFilenamePath) : templateFilenamePath;
			if (usageContext == UsageContext.SERVER_SIDE && (Files.notExists(formsPath) || !Files.isRegularFile(formsPath))) {
				throw new FileNotFoundException("Unable to find template (" + formsPath.toString() + ").");
			}
		}
		return new TemplateValues(contentRoot, templateFilenamePath);
	}

	public PathOrUrl getContentRoot() {
		return contentRoot;
	}

	public Path getTemplate() {
		return template;
	}
}
