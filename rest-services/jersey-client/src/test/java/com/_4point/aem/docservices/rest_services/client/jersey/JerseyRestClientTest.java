package com._4point.aem.docservices.rest_services.client.jersey;

/**
 * RestClient testing is performed by AbstractRestClientTest, which is extended by this class.  
 * 
 * This class provides a JerseyRestClient factory method to the AbstractRestClientTest class, 
 * which performs the actual testing.
 * 
 * The AbstractRestClientTest class is a copy of the one in the Spring Boot autoconfiguration project.
 * 
 */
class JerseyRestClientTest extends AbstractRestClientTest {
	
	JerseyRestClientTest() {
		super(JerseyRestClient::factory);
	}
}
