package com._4point.aem.docservices.rest_services.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ByteArrayStringTest {

	ByteArrayString underTest;
	
	@Test
	void testToString() {
		underTest = new ByteArrayString("12345".getBytes(), 4);
		assertEquals("ByteArrayString [data=[49, 50, 51, 52], ascii=[1, 2, 3, 4]]", underTest.toString());
	}

}
