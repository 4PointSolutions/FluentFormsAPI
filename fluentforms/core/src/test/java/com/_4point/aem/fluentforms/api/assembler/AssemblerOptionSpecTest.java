package com._4point.aem.fluentforms.api.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;

public class AssemblerOptionSpecTest {
	
	private final static com.adobe.fd.assembler.client.AssemblerOptionSpec emptyAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
	
	private final AssemblerOptionsSpecImpl underTest = new AssemblerOptionsSpecImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobeAssemblerSpecOptions_NoChanges() {
		
		assertEmpty(underTest);
	}

	private void assertEmpty(AssemblerOptionsSpecImpl assemblerOptionsSpecImpl) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec  = AdobeAssemblerServiceAdapter.toAdobeAssemblerOptionSpec(assemblerOptionsSpecImpl);
		assertEquals(emptyAssemblerOptionSpec.isFailOnError(), adobeAssemblerOptionSpec.isFailOnError());
		assertEquals(emptyAssemblerOptionSpec.getDefaultStyle(), adobeAssemblerOptionSpec.getDefaultStyle());
		assertEquals(emptyAssemblerOptionSpec.getFirstBatesNumber(), adobeAssemblerOptionSpec.getFirstBatesNumber());
		assertEquals(emptyAssemblerOptionSpec.getLogLevel(), adobeAssemblerOptionSpec.getLogLevel());
		assertEquals(emptyAssemblerOptionSpec.isValidateOnly(), adobeAssemblerOptionSpec.isValidateOnly());
		assertEquals(emptyAssemblerOptionSpec.isTakeOwnership(), adobeAssemblerOptionSpec.isTakeOwnership());
	}
	
	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are the not same as an empty options object.")
	void testToAdobeAssemblerSpecOptions_AllChanges() {
		underTest.setFailOnError(false);
		underTest.setDefaultStyle("abc");
		underTest.setFirstBatesNumber(1);
		underTest.setTakeOwnership(true); 
		underTest.setValidateOnly(true);
		underTest.setLogLevel("DEBUG"); 
		assertNotEmpty(underTest);
	}

	private void assertNotEmpty(AssemblerOptionsSpecImpl assemblerOptionsSpecImpl) {
	    assertNotEquals(emptyAssemblerOptionSpec.isFailOnError(), assemblerOptionsSpecImpl.isFailOnError());
	    assertNotEquals(emptyAssemblerOptionSpec.getDefaultStyle(), assemblerOptionsSpecImpl.getDefaultStyle());
	    assertNotEquals(emptyAssemblerOptionSpec.getFirstBatesNumber(), assemblerOptionsSpecImpl.getFirstBatesNumber());
	    assertNotEquals(emptyAssemblerOptionSpec.getLogLevel(), assemblerOptionsSpecImpl.getLogLevel());
		assertNotEquals(emptyAssemblerOptionSpec.isValidateOnly(), assemblerOptionsSpecImpl.isValidateOnly());
		assertNotEquals(emptyAssemblerOptionSpec.isTakeOwnership(), assemblerOptionsSpecImpl.isTakeOwnership());
		 
	}

	
}
