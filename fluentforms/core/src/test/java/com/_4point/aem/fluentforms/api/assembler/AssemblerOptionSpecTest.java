package com._4point.aem.fluentforms.api.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;

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
		assertEquals(emptyAssemblerOptionSpec.getFirstBatesNumber(), adobeAssemblerOptionSpec.getFirstBatesNumber()-1);
		assertEquals(emptyAssemblerOptionSpec.getLogLevel(), adobeAssemblerOptionSpec.getLogLevel().toString());
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
		underTest.setLogLevel(LogLevel.FINER); 
		assertNotEmpty(underTest);
	}

	static void assertNotEmpty(AssemblerOptionsSpec assemblerOptionsSpec) {
	    assertNotEquals(emptyAssemblerOptionSpec.isFailOnError(), assemblerOptionsSpec.isFailOnError());
	    assertNotEquals(emptyAssemblerOptionSpec.getDefaultStyle(), assemblerOptionsSpec.getDefaultStyle());
	    assertNotEquals(emptyAssemblerOptionSpec.getFirstBatesNumber(), assemblerOptionsSpec.getFirstBatesNumber());
	    assertNotEquals(emptyAssemblerOptionSpec.getLogLevel(), assemblerOptionsSpec.getLogLevel());
		assertNotEquals(emptyAssemblerOptionSpec.isValidateOnly(), assemblerOptionsSpec.isValidateOnly());
		assertNotEquals(emptyAssemblerOptionSpec.isTakeOwnership(), assemblerOptionsSpec.isTakeOwnership());
		 
	}

	
}
