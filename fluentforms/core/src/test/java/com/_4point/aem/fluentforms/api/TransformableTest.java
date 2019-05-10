package com._4point.aem.fluentforms.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransformableTest {

	private static final String INITIAL_VALUE = "InitialValue";
	private static final String NEW_VALUE = "NewValue";
	
	class TestClass implements Transformable<TestClass>{
		String propA;
		
		public TestClass(String propA) {
			super();
			this.propA = propA;
		}
		
		public String getPropA() {
			return propA;
		}
		public TestClass setPropA(String propA) {
			this.propA = propA;
			return this;
		}
	}
	
	TestClass underTest = new TestClass(INITIAL_VALUE);
	
	@Test
	void test_WithValue() {
		TestClass result = underTest.transform(b->Transformable.setIfNotNull(b, b::setPropA, NEW_VALUE));
		assertEquals(NEW_VALUE, result.getPropA());
	}

	@Test
	void test_WithNull() {
		TestClass result = underTest.transform(b->Transformable.setIfNotNull(b, b::setPropA, null));
		assertEquals(INITIAL_VALUE, result.getPropA());
	}

}
