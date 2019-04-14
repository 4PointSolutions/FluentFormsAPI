package com._4point.aem.fluentforms.impl;

import java.util.function.Consumer;

public class BuilderUtils {
	
	private BuilderUtils() {} // Prevent someone from instantiating this class.

	public static <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}
	

}
