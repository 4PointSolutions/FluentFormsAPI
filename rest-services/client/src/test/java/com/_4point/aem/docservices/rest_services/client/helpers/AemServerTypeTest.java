package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType.StandardType;

class AemServerTypeTest {

	
	private enum StandardTypeScenario {
		JEE_LOWER("jee", StandardType.JEE), 
		JEE_UPPER("JEE", StandardType.JEE), 
		JEE_MIXED("jEe", StandardType.JEE), 
		OSGI_LOWER("osgi", StandardType.OSGI),
		OSGI_UPPER("OSGI", StandardType.OSGI),
		OSGI_MIXED("osGi", StandardType.OSGI),
		;
		private String inputString;
		private StandardType expectedResult;

		private StandardTypeScenario(String inputString, StandardType expectedResult) {
			this.inputString = inputString;
			this.expectedResult = expectedResult;
		}
	}

	@ParameterizedTest
	@EnumSource
	void testStandardTypeFromLegit(StandardTypeScenario scenario) {
		// Make sure that strings that should return results return the correct result.
		assertEquals(scenario.expectedResult, StandardType.from(scenario.inputString).get());
	}

	@ParameterizedTest
	@ValueSource(strings = {"foo", "   ", "!@)($*&(#"})
	@NullAndEmptySource
	void testStandardTypeFromNonLegit(String inputString) {
		// Make sure that strings that don't match (or are empty or null) return no result.
		assertFalse(StandardType.from(inputString).isPresent());
	}
	
	@Test
	void testScenarios() {
		// This test makes sure our test scenariod cover all possible StandardTypes 
		final Set<StandardType> typesTested = Arrays.stream(StandardTypeScenario.values()).map(t->t.expectedResult).collect(Collectors.toSet());
		final Set<StandardType> typesAvailable = Arrays.stream(StandardType.values()).collect(Collectors.toSet());
		
		assertTrue(typesTested.containsAll(typesAvailable), "Scenarios do not cover all StandardTypes, please update the list of scenarios.");
	}

}
