package com._4point.aem.fluentforms.api.forms;

import java.nio.file.Path;

public interface ValidationOptions {

	Path getContentRoot();

	Path getDebugDir();

	com.adobe.fd.forms.api.ValidationOptions toAdobeValidationOptions();

}