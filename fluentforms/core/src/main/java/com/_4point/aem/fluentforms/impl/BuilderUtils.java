package com._4point.aem.fluentforms.impl;

import java.util.function.Consumer;

/**
 * Most of the structures that are used as parameters in the AEM Java API have members that are Object references, if the  
 * value has not been explicity set, then the value in the parameter is null and AEM uses the default value.  When we are
 * setting the values in these structures, we do not want to set the value of that parameter to something explicit so that
 * we don't have to build knowledge into our code of what the default values are.
 * 
 * This class provides a utility function that is useful for no setting a value if the associated parameter is null (i.e. the
 * user has not set that parameter in the fluentforms API).
 */
public class BuilderUtils {
	
	private BuilderUtils() {} // Prevent someone from instantiating this class.

	public static <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}
	

}
