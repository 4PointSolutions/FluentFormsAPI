package com._4point.aem.fluentforms.impl;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
	private static final String URL_SEPARATOR = "/";
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
	private static TemplateValues determineTemplateValues(Path template, PathOrUrl templatesDir, UsageContext usageContext) throws FileNotFoundException {
		Path templateParentDir = template.getParent();
		PathOrUrl contentRoot;
		if (templatesDir == null && templateParentDir != null) {
			// No templatesDir but there's a parent dir on the template
			// use the parent dir as the content root
			contentRoot = PathOrUrl.from(templateParentDir);
		} else if (templatesDir != null && templateParentDir == null) {
			// There's a templatesDir but no parent dir on the template
			// so just use the templatesDir as the content root
			contentRoot = templatesDir;
		} else if (templatesDir != null && templateParentDir != null) {
			// There's a templatesDir and there's a parent dir on the template
			// append the parent dir onto the templates dir to create the content root
			// unless the parent dir is an absolute path.
			if (templateParentDir.isAbsolute()) {
				contentRoot = PathOrUrl.from(templateParentDir);
			} else if (templatesDir.isPath()) {
				contentRoot = PathOrUrl.from(templatesDir.getPath().resolve(templateParentDir));
			} else if (templatesDir.isUrl()) {
				contentRoot = PathOrUrl.from(stripTrailingSlash(templatesDir.getUrl().toString()) + URL_SEPARATOR + templateParentDir.toString());
			} else if (templatesDir.isCrxUrl()) {
				contentRoot = PathOrUrl.from(stripTrailingSlash(templatesDir.getCrxUrl()) + URL_SEPARATOR + templateParentDir.toString());
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

	/**
	 * Reconcile the template and context root provided by the user.
	 * 
	 * The references in XDPs are typically relative to the location of the form.  Therefore, the context root should be set to the
	 * directory where the form resides.  Sometimes however, the template is provided with one or more parent directories.  We need to
	 * shift those parent directories over into the contextRoot and remove them from the template name (so that the relative links inside
	 * the template work).  Also, sometimes no contentRoot is supplied.  In this case, if the template path is absolute, then again,
	 * the absolute path to the template parent directory should be placed in the context root and only the filename passed in the 
	 * template parameter.
	 * 
	 * This routine compares the values of template parameter and the templatesDir (original context root) and reconciles them.  If there
	 * needs to be an adjustment, then it returns a TemplateValues object that contains the adjusted values.
	 * 
	 *  Note: When a Path is passed in (i.e. <code>PathOrUrl.isPath == true</code>) as the template value, there will always
	 *        be a return (although the values may remain unchanged). 
	 * 
	 * @param template			original template value
	 * @param templatesDir		original context root value
	 * @param usageContext 		whether this is server or cient side processing
	 * @return 					the revised template and contextRoot values (if they can be reconciled), otherwise Optional.empty().
	 * @throws FileNotFoundException thrown when a Path is passed in for both template and templatesDir but the resulting template/context root does not exist on the server.
	 */
	public static Optional<TemplateValues> determineTemplateValues(PathOrUrl template, PathOrUrl templatesDir, UsageContext usageContext) throws FileNotFoundException {
		if (template.isPath()) {
			return Optional.of(determineTemplateValues(template.getPath(), templatesDir, usageContext));
		} else {
			Optional<String> templateFilename = template.getFilename();
			if (templatesDir == null && templateFilename.isPresent()) {
				// Since template is not a Path, it must be absolute and with no templates dir, we can treat the filename of the URL or CRXURL like a file path.     
				return Optional.of(determineTemplateValues(Paths.get(templateFilename.get()), template.getParent().orElse(null), usageContext));
			} else {
				// Since the templatesDir (contextRoot) is not empty and templates is not a Path (and therefore absolute),
				// no reconciliation is possible
				return Optional.empty();
			}
		}
	}

	public PathOrUrl getContentRoot() {
		return contentRoot;
	}

	public Path getTemplate() {
		return template;
	}
	
	// Exposed for testing.
	/* package */ static String stripTrailingSlash(String pathOrUrl) {
		int length = pathOrUrl.length();
		if (length > 0) {
			String lastChar = pathOrUrl.substring(length - 1, length);
			if (URL_SEPARATOR.equals(lastChar)) {
				return pathOrUrl.substring(0, length - 1);
			}
		}
		return pathOrUrl;
	}
}
