package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import com._4point.aem.fluentforms.api.PathOrUrl;

public class Converters {

	@Configuration
	public static class PathConverter implements Converter<String, Path> {

		@Override
		public Path convert(String source) {
			return Path.of(source);
		}
		
	}
	
	@Configuration
	public static class PathOrUrlConverter implements Converter<String, PathOrUrl> {

		@Override
		public PathOrUrl convert(String source) {
			return PathOrUrl.from(source);
		}
		
	}
}
