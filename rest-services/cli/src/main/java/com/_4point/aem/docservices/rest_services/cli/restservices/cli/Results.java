package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is a generic class for storing the results of an AEM operation.  All commends store their results in
 * a global results object.  The user can then perform commands to on those results afterwards (like list/save)
 *
 * @param operation 	The operation that generated this result
 * @param primary		The main result (usually a PDF)
 * @param secondary		Secondary results that may also be available
 * @param timestamp		TImestamp of when the results were generated.
 */
public record Results(String operation, Result primary, Map<String, Result> secondary, Instant timestamp) {
	public Results(String operation, Result primary, Map<String, Result> secondary) {
		this(operation, primary, secondary, Instant.now());
	}
	public Results(String operation, Result primary) {
		this(operation, primary, Map.of());
	}
	
	public record MediaType(String type, String subType, Optional<Charset> charSet) {
		public MediaType(String type, String subType) {
			this(type, subType, Optional.empty());
		}
		
		private static final String APPLICATION = "application";
		private static final String TEXT = "text";
		private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
		
		public static final MediaType APPLICATION_PDF = new MediaType(APPLICATION, "pdf");
		public static final MediaType TEXT_PLAIN = new MediaType(TEXT, "plain", Optional.of(DEFAULT_CHARSET));
		
		@Override
		public String toString() {
			String myType = type + "/" + subType;
			return charSet.map(cs->myType + "; charset=" + cs.displayName().toLowerCase()).orElse(myType);
		}
	};

	public record Result(byte[] data, MediaType mediaType, Optional<Path> filename) {
		public String summary() {
			String media = "MediaType='" + mediaType + "'";
			return filename.map(p->media + "; Filename='" + p + "'").orElse(media);
		}
		
		public static Result ofPdf(byte[] data, Optional<Path> filename) { return new Result(data, MediaType.APPLICATION_PDF, filename); }
		public static Result ofPdf(byte[] data) { return new Result(data, MediaType.APPLICATION_PDF, Optional.empty()); }
		public static Result ofText(String data, Optional<Path> filename) { return new Result(data.getBytes(MediaType.DEFAULT_CHARSET), MediaType.TEXT_PLAIN, filename); }
		public static Result ofText(String data) { return new Result(data.getBytes(MediaType.DEFAULT_CHARSET), MediaType.TEXT_PLAIN, Optional.empty()); }
	};
	
	public static Results ofPdf(String operation, byte[] data, Optional<Path> filename) { return new Results(operation, Result.ofPdf(data, filename));}
	public static Results ofPdf(String operation, byte[] data) { return new Results(operation, Result.ofPdf(data));}
	public static Results ofText(String operation, String data, Optional<Path> filename) { return new Results(operation, Result.ofText(data, filename));}
	public static Results ofText(String operation, String data) { return new Results(operation, Result.ofText(data));}
	
	
	/**
	 * Return one String per result.
	 * 
	 * The primary results is always in the first entry.
	 * 
	 * @return
	 */
	public List<String> display() {
		return Stream.concat(
				Stream.of(formatResult(operation, primary)), 									// Start with the primary result
				secondary.entrySet().stream().map(e->formatResult(e.getKey(), e.getValue()))	// Add all the secondary results
				).toList();
				
	}
	
	private static String formatResult(String operation, Result result) {
		return "'" + operation + "' -> " + result.summary();
	}
	
	public static ResultsBuilder builder(String operation, Result primary) {
		return new ResultsBuilder(operation, primary);
	}

	public static Results build(String operation, Result primary, Function<ResultsBuilder, ResultsBuilder> fn) {
		return fn.apply(new ResultsBuilder(operation, primary)).build();
	}

	public static class ResultsBuilder {
		private String operation;
		private Result primary;
		private Map<String, Result> secondary = new LinkedHashMap<>();

		private ResultsBuilder(String operation, Result primary) {
			this.operation = operation;
			this.primary = primary;
		}
		
		public ResultsBuilder addSecondary(String qualifier, Result secondary) {
			this.secondary.put(qualifier, secondary);
			return this;
		}
		
		public Results build() {
			return new Results(operation, primary, Collections.unmodifiableMap(secondary));
		}
	}
}
