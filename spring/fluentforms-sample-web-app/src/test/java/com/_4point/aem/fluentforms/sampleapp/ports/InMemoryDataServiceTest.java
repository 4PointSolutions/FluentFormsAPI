package com._4point.aem.fluentforms.sampleapp.ports;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;

class InMemoryDataServiceTest extends AbstractDataServiceTest{

	protected InMemoryDataServiceTest() {
		super(new InMemoryDataService(), new String[] {"Error retrieving data, key does not exist"}, new String[] {"Error saving data, key already exists"});
	}

}
