package com._4point.aem.fluentforms.spring.rest_services.client;

/**
 * RestClient testing is performed by AbstractRestClientTest, which is extended by this class.  
 * 
 * This class provides a SpringRestClient factory method to the AbstractRestClientTest class, 
 * which performs the actual testing.
 * 
 * The AbstractRestClientTest class is a copy of the one in the JerseyRestClient project.
 * 
 */
class SpringRestClientRestClientTest extends AbstractRestClientTest {
	
	SpringRestClientRestClientTest() {
		super(SpringRestClientRestClient::factory);
	}
}
