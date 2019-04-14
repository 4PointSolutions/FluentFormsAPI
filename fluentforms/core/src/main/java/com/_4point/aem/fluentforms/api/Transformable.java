package com._4point.aem.fluentforms.api;

import java.util.function.Function;

public interface Transformable<T extends Transformable<T>> {

	@SuppressWarnings("unchecked")
	public default T transform(Function<T, T> function) {
        return function.apply((T) this);
    }
}
