package com._4point.aem.fluentforms.sampleapp.ports;

class InMemoryDataServiceTest extends AbstractDataServiceTest{

	protected InMemoryDataServiceTest() {
		super(new InMemoryDataService(), new String[] {"Error retrieving data, key does not exist"}, new String[] {"Error saving data, key already exists"});
	}
}
