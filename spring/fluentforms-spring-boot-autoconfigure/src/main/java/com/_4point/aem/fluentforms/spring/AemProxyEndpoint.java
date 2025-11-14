package com._4point.aem.fluentforms.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com._4point.aem.docservices.rest_services.client.helpers.ReplacingInputStream;

/**
 * Reverse Proxy Code which reverse proxies secondary resources (.css, .js, etc.) that the browser will request.
 * These requests are forwarded to AEM.
 * 
 * This code relies on Eclipse Jersey and expects that the spring-boo-starter-jersey is included in the project.
 * 
 * It assumes that the application that generated the Adaptive Form or HTML5 Form inserted /aem in from of any
 * AEM links in the AF or HTML5 html code.  This task is typically performes using the FluentForms 
 * StandardFormsFeederUrlFilters.getStandardInputStreamFilter() method and passing that into the call to 
 * get the AdaptiveForm or HTML5 Form using the FLuentForms libraries.
 *
 */
@CrossOrigin
@RestController
@RequestMapping("/aem")
public class AemProxyEndpoint {

	private final static Logger logger = LoggerFactory.getLogger(AemProxyEndpoint.class);

	private static final String AEM_APP_PREFIX = "/";
	private final RestClient httpClient;

	private final AemProxyConfiguration aemProxyConfig;
	private final AemConfiguration aemConfig;

    /**
     * 
     */
    public AemProxyEndpoint(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig, RestClientSsl restClientSsl) {
    	this.aemProxyConfig = aemProxyConfig;
    	this.aemConfig = aemConfig;
    	this.httpClient = createClient(aemConfig, RestClient.builder(), restClientSsl);
	}

    @GetMapping("/libs/granite/csrf/token.json")
    public ResponseEntity<byte[]> proxyOsgiCsrfToken() throws IOException {
    	final String path = AEM_APP_PREFIX + "libs/granite/csrf/token.json";
    	return getCsrfToken(path);
    }

    @GetMapping("/lc/libs/granite/csrf/token.json")
	public ResponseEntity<byte[]> proxyJeeCsrfToken() throws IOException {
		final String path = "/lc/libs/granite/csrf/token.json";
	  	return getCsrfToken(path);
	}

	private ResponseEntity<byte[]> getCsrfToken(final String path) {
		logger.atDebug().log("Proxying GET request. CSRF token");
		
		URI uri = UriComponentsBuilder.fromUriString(aemConfig.url())
									  .path(path)
									  .build()
									  .toUri();

		logger.atDebug().log(()->"Proxying GET request for CSRF token '" + uri.toString() + "'.");
		ResponseEntity<byte[]> response = httpClient.get()
													.uri(uri)
													.retrieve()
													.toEntity(byte[].class);

		logger.atDebug()
			  .addArgument(()->response.getStatusCode().toString())
			  .log(()->"CSRF token GET response status = {}");
		
        logger.atDebug().log("Returning GET response for CSRF token.");
		return response;
	}

	/**
     * This function acts as a reverse proxy for anything under clientlibs.  It just forwards
     * anything it receives on AEM and then returns the response.  
     * 
     * @param remainder
     * @return
     * @throws ConfigurationException
     */
    @GetMapping("/{*remainder}")
    public ResponseEntity<byte[]> proxyGet(@PathVariable("remainder") String remainder) {
    	logger.atDebug().log(()->"Proxying GET request. remainder=" + remainder);
		URI uri = UriComponentsBuilder.fromUriString(aemConfig.url())
				  					  .path(AEM_APP_PREFIX + remainder)
				  					  .build()
				  					  .toUri();
		logger.atDebug().log(()->"Proxying GET request for target '" + uri.toString() + "'.");
		ResponseEntity<byte[]> response = httpClient.get()
													.uri(uri)
													.retrieve()
													.toEntity(byte[].class);

		if (logger.isDebugEnabled()) {
			response.getHeaders().forEach((h, l)->logger.atDebug().log("For " + uri + ", Header:" + h + "=" + l.stream().map(o->(String)o).collect(Collectors.joining("','", "'", "'"))));
		}

		logger.atDebug().log(()->"Returning GET response from target '" + uri + "' status code=" + response.getStatusCode().value() + ".");
		Function<InputStream, InputStream> filter = switch (remainder) {
		 	case "/etc.clientlibs/clientlibs/granite/utils.js" -> this::substituteAfBaseLocation;
			case "/etc.clientlibs/fd/xfaforms/clientlibs/profile.js" -> AemProxyEndpoint::fixTogglesDotJsonLocation;
			default -> is -> is; // No filtering needed
		};
		return ResponseEntity.status(response.getStatusCode())
				.headers(response.getHeaders())
				.header("Transfer-Encoding", new String[0])
				.body(filterByteArray(response.getBody(), filter));
    }

    // passes a byte array through an InputStream filter and returns the result as a byte array.
    private static byte[] filterByteArray(byte[] input, Function<InputStream, InputStream> isFilter) {
    	try (var bais = new ByteArrayInputStream(input)) {
    		return isFilter.apply(bais).readAllBytes();
    	} catch (IOException e) {
			throw new IllegalStateException("This should never happen - ", e);
		}
    }
    
    /**
     * Wraps an InputStream with a wrapper that replaces some code in the Adobe utils.js code.
     * 
     * The detectContextPath function in utils.js has the following line: 
     * contextPath = result[1];
     * 
     * This routine replaces it with
     * contextPath = FORMSFEEDER_AF_BASE_LOCATION_PROP + result[1];
     * (where FORMSFEEDER_AF_BASE_LOCATION_PROP is whatever value is in the application.properties file)
     * 
     * @param is
     * @return
     */
    private InputStream substituteAfBaseLocation(InputStream is) {
    	if (aemProxyConfig.afBaseLocation().isBlank()) {
    		return is;
    	} else {
    		String target = "contextPath = result[1];";
			String replacement = "contextPath = \""+ aemProxyConfig.afBaseLocation() + "\" + result[1];";
			logger.atDebug().log("Altering granite/utils.js to replace '{}' with '{}'", target, replacement);
			return new ReplacingInputStream(is, target, replacement);
    	}
    }
    
	private static InputStream fixTogglesDotJsonLocation(InputStream is) {
		String target = "\"/etc.clientlibs/toggles.json\"";
		String replacement = "\"/aem/etc.clientlibs/toggles.json\"";
		logger.atDebug().log("Altering profile.js to replace '{}' with '{}'", target, replacement);
		return new ReplacingInputStream(is, target, replacement);
	}
    
    @PostMapping("/{*remainder}")
    public ResponseEntity<byte[]> proxyPost(@PathVariable("remainder") String remainder, @RequestHeader(value = "Content-Type", required = false) String contentType, byte[] in) {
    	logger.atDebug().log("Proxying POST request. remainder={}", remainder);
		URI uri = UriComponentsBuilder.fromUriString(aemConfig.url())
				  .path(AEM_APP_PREFIX + remainder)
				  .build()
				  .toUri();
		logger.atDebug().addArgument(()->uri.toString())
			  			.addArgument(contentType)
			  			.log(()->"Proxying POST request for target '{}'.  ContentType='{}'.");
	
		ResponseEntity<byte[]> response = httpClient.post()
										.uri(uri)
										.body(debugInput(Objects.requireNonNullElseGet(in, ()->new byte[0]), uri.toString())) // if Debug is on, write out information about input stream 
										.contentType(contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_OCTET_STREAM) // supply default content type if it was omitted.
										.retrieve()
										.toEntity(byte[].class);

		if (remainder.contains("af.submit.jsp")) {
			logger.atDebug().addArgument(()->Boolean.valueOf(response == null).toString())
							.log("result == null is {}.");
			MediaType mediaType = response.getHeaders().getContentType();
			logger.atDebug()
				  .addArgument(()->uri.toString())
				  .addArgument(()->mediaType != null ? mediaType.toString() : "")
				  .addArgument(()->response.getHeaders().getFirst("Transfer-Encoding"))
				  .log("Returning POST response from target '{}'. contentType='{}'.  transfer-encoding='{}'.");
		} else {
			logger.atDebug()
				  .addArgument(uri::toString)
				  .log("Returning POST response from target '{}'.");
		}
		
		return response;
    }
    
    private static byte[] debugInput(byte[] inputBytes, String target) {
		logger.atDebug()
			  .log("Proxying POST request for target '{}'.  numberOfBytes proxied='{}'.", target, inputBytes.length);
		logger.atTrace()
			  .addArgument(target)
			  .addArgument(()->new String(inputBytes, StandardCharsets.UTF_8))
			  .log("Proxying POST request for target '{}'.  input bytes proxied='{}'.");
		return inputBytes;
    }
    
	private static RestClient createClient(
			AemConfiguration aemConfig, 
			RestClient.Builder builder,
			RestClientSsl restClientSsl
			) {

		if (aemConfig.useSsl()) {
			configureSsl(builder, restClientSsl, aemConfig.sslBundle()); 
		} else {
			logger.info("Creating default client.");
		}
		
		if (aemConfig.user() != null) {
			configureBasicAuth(builder, aemConfig.user(), aemConfig.password());
		}
		
		return builder.baseUrl(aemConfig.url())
					  .build();
	}

	private static void configureBasicAuth(RestClient.Builder builder, String username, String password) {
		builder.requestInterceptor(new BasicAuthenticationInterceptor(username, password));
	}

	private static void configureSsl(RestClient.Builder builder, RestClientSsl restClientSsl, String bundleName) {
		if (restClientSsl != null && bundleName != null) {
			logger.info("Using Client ssl bundle: '" + bundleName + "'.");
			try {
				builder.apply(restClientSsl.fromBundle(bundleName));
			} catch (NoSuchSslBundleException e) {
				// Eat the exception and fall through to the default client
				// Default the SSL context (which includes the default trust store)
				logger.warn("Unable to locate ssl bundle '" + bundleName + "'. Creating default client.");
			}
		} else if (restClientSsl == null && bundleName != null) {
			throw new IllegalStateException("RestClientSsl is null, unable to configure SSL bundle '" + bundleName + "'.");
		} else { /* bundlename == null  */
			logger.info("AEM bundleName is null. Creating default client.");
		}
	}
}
