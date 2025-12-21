package com._4point.aem.fluentforms.spring;

import java.nio.file.Path;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import com._4point.aem.fluentforms.api.PathOrUrl;

/**
 * Spring Converters used in Spring's auto conversion mechanism.  In this case used to convert Strings
 * to java.nio.file.Path or FluentForm's PathOrUrl objects.
 */
public class Converters {

	@Configuration
	public static class PathConverter implements Converter<String, Path> {

		@Override
		public Path convert(@Nullable String source) {
			return Path.of(source == null ? "" : source);
		}
		
	}
	
	@Configuration
	public static class PathOrUrlConverter implements Converter<String, PathOrUrl> {

		@Override
		public PathOrUrl convert(@Nullable String source) {
			return PathOrUrl.from(source == null ? "" : source);
		}
		
	}
}
