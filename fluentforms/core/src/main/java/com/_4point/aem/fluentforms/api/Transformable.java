package com._4point.aem.fluentforms.api;

import java.util.function.Function;

public interface Transformable<T extends Transformable<T>> {

	@SuppressWarnings("unchecked")
	public default T transform(Function<T, T> function) {
        return function.apply((T) this);
    }

	// While this isn't strictly related to transformable, this is a handy place to put this helper function.
	// Any time we 
	public static <T, B> B setIfNotNull(final B builder, final Function<T, B> setter, final T value) {
		return value != null ? setter.apply(value) : builder;
	}

}
