package com._4point.aem.fluentforms.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Path("/aem")
public class AemProxyEndpoint {

	private final static Logger logger = LoggerFactory.getLogger(AemProxyEndpoint.class);

	private static final String AEM_APP_PREFIX = "/";
	private Client httpClient;

	private final AemProxyConfiguration aemProxyConfig;
	private final AemConfiguration aemConfig;

    /**
     * 
     */
    public AemProxyEndpoint(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig) {
    	this.aemProxyConfig = aemProxyConfig;
    	this.aemConfig = aemConfig;
    	this.httpClient = ClientBuilder.newClient().register(HttpAuthenticationFeature.basic(aemConfig.servername(), aemConfig.password())).register(MultiPartFeature.class);
	}

    @Path("libs/granite/csrf/token.json")
    @GET
    public ChunkedOutput<byte[]> proxyOsgiCsrfToken() throws IOException {
    	final String path = AEM_APP_PREFIX + "libs/granite/csrf/token.json";
    	return getCsrfToken(path);
    }

    @Path("lc/libs/granite/csrf/token.json")
	@GET
	public ChunkedOutput<byte[]> proxyJeeCsrfToken() throws IOException {
		final String path = "/lc/libs/granite/csrf/token.json";
	  	return getCsrfToken(path);
	}

	private ChunkedOutput<byte[]> getCsrfToken(final String path) {
		logger.debug("Proxying GET request. CSRF token");
		WebTarget webTarget = httpClient.target(aemConfig.url())
								.path(path);
		logger.debug("Proxying GET request for CSRF token '" + webTarget.getUri().toString() + "'.");
		Response result = webTarget.request()
		   .get();

		logger.debug("CSRF token GET response status = " + result.getStatus());
		final ChunkedInput<byte[]> chunkedInput = result.readEntity(new GenericType<ChunkedInput<byte[]>>() {});
		final ChunkedOutput<byte[]> output = new ChunkedOutput<byte[]>(byte[].class);
		
		new Thread() {
            public void run() {
            	try {
					try (chunkedInput; output) {
					    byte[] chunk;
 
					    while ((chunk = chunkedInput.read()) != null) {
					        output.write(chunk);
					        logger.debug("Returning GET chunk for CSRF token.");
					    }
					}
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}
            }
        }.start();
		
        logger.debug("Returning GET response for CSRF token.");
		return output;
	}



	/**
     * This function acts as a reverse proxy for anything under clientlibs.  It just forwards
     * anything it receives on AEM and then returns the response.  
     * 
     * @param remainder
     * @return
     * @throws ConfigurationException
     */
    @Path("{remainder : .+}")
    @GET
    public Response proxyGet(@PathParam("remainder") String remainder) {
    	logger.debug("Proxying GET request. remainder=" + remainder);
		WebTarget webTarget = httpClient.target(aemConfig.url())
								.path(AEM_APP_PREFIX + remainder);
		logger.debug("Proxying GET request for target '" + webTarget.getUri().toString() + "'.");
		Response result = webTarget.request()
		   .get();
		if (logger.isDebugEnabled()) {
			result.getHeaders().forEach((h, l)->logger.debug("For " + webTarget.getUri().toString() + ", Header:" + h + "=" + l.stream().map(o->(String)o).collect(Collectors.joining("','", "'", "'"))));
		}

		logger.debug("Returning GET response from target '" + webTarget.getUri().toString() + "' status code=" + result.getStatus() + ".");
		
		return Response.fromResponse(result)
					   .header("Transfer-Encoding", null)			// Remove the Transfer-Encoding header
					   .entity(result.readEntity(InputStream.class))
					   .build();
    }

    /**
     * The following routine replaces a line in the utils.js file to enable links with absolute URLs.
     * 
     * @return
     */
    @Path("/etc.clientlibs/clientlibs/granite/utils.js")
    @GET
    public Response proxyGet_Utils_Js() {
    	logger.debug("Proxying Utils GET request. remainder=/etc.clientlibs/clientlibs/granite/utils.js");
		WebTarget webTarget = httpClient.target(aemConfig.url())
								.path(AEM_APP_PREFIX + "etc.clientlibs/clientlibs/granite/utils.js");
		logger.debug("Proxying Utils GET request for target '" + webTarget.getUri().toString() + "'.");
		Response result = webTarget.request()
		   .get();
		
//		System.out.println("Received GET response from target '" + webTarget.getUri().toString() + "'. contentType='" + result.getMediaType().toString() + "'.  transfer-encoding='" + result.getHeaderString("Transfer-Encoding") + "'.");
		logger.debug("Returning Utils GET response from target '" + webTarget.getUri().toString() + "' status code=" + result.getStatus() + ".");
		
		return Response.fromResponse(result)
					   .entity(substituteAfBaseLocation(result.readEntity(InputStream.class)))
					   .build();
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
    		return new ReplacingInputStream(is, "contextPath = result[1];", "contextPath = \""+ aemProxyConfig.afBaseLocation() + "\" + result[1];");
    	}
    }
    
    @Path("{remainder : .+}")
    @POST
    public Response proxyPost(@PathParam("remainder") String remainder, @HeaderParam("Content-Type") String contentType, InputStream in) {
    	logger.debug("Proxying POST request. remainder={}", remainder);
		WebTarget webTarget = httpClient.target(aemConfig.url())
								.path(AEM_APP_PREFIX + remainder);
		logger.debug("Proxying POST request for target '{}'.  ContentType='{}'.", webTarget.getUri().toString(), contentType );
		Response result = webTarget.request()
				.post(Entity.entity(
						logger.isDebugEnabled() ? debugInput(in, webTarget.getUri().toString()) : in,	// if Debug is on, write out information about input stream 
						contentType != null ? contentType : "application/octet-stream"					// supply default content type if it was omitted.
						));

		if (remainder.contains("af.submit.jsp")) {
			logger.debug("result == null is {}.", Boolean.valueOf(result == null).toString());
			MediaType mediaType = result.getMediaType();
			logger.debug("Returning POST response from target '{}'. contentType='{}'.  transfer-encoding='{}'.", webTarget.getUri().toString(), mediaType != null ? mediaType.toString() : "", result.getHeaderString("Transfer-Encoding"));
		} else {
			logger.debug("Returning POST response from target '{}'.", webTarget.getUri().toString());
		}
		
		return Response.fromResponse(result).build();
    }
    
    private InputStream debugInput(InputStream in, String target) {
		try {
			byte[] inputBytes = in.readAllBytes();
			logger.debug("Proxying POST request for target '{}'.  numberOfBytes proxied='{}'.", target, inputBytes.length );
			logger.trace("Proxying POST request for target '{}'.  input bytes proxied='{}'.", target, new String(inputBytes, StandardCharsets.UTF_8) );
			return new ByteArrayInputStream(inputBytes);
		} catch (IOException e) {
			logger.error("Error reading input stream.", e);
			return new ByteArrayInputStream(new byte[0]);
		}
    }
}
