package com._4point.aem.fluentforms.api.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.impl.assembler.AdobeDocAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;

public class AssemblerOptionSpecTest {
	private final static com.adobe.fd.assembler.client.AssemblerOptionSpec emptyAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
	
	private final AssemblerOptionsSpecImpl underTest = new AssemblerOptionsSpecImpl();

	@Test
	@DisplayName("Make sure that if nothiAdobeOutputServiceAdapterng was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobeAssemblerSpecOptions_NoChanges() {
		
		assertEmpty(underTest);
	}

	private void assertEmpty(AssemblerOptionsSpecImpl assemblerOptionsSpecImpl) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec  = AdobeDocAssemblerServiceAdapter.toAdobeAssemblerOptionSpec(assemblerOptionsSpecImpl);
		assertEquals(emptyAssemblerOptionSpec.isFailOnError(), adobeAssemblerOptionSpec.isFailOnError());
		assertEquals(emptyAssemblerOptionSpec.getDefaultStyle(), adobeAssemblerOptionSpec.getDefaultStyle());
		assertEquals(emptyAssemblerOptionSpec.getFirstBatesNumber(), adobeAssemblerOptionSpec.getFirstBatesNumber());
		assertEquals(emptyAssemblerOptionSpec.getLogLevel(), adobeAssemblerOptionSpec.getLogLevel());
	}
	
	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are the not same as an empty options object.")
	void testToAdobeAssemblerSpecOptions_AllChanges() {
		underTest.setFailOnError(false);
		assertNotEmpty(underTest);
	}

	private void assertNotEmpty(AssemblerOptionsSpecImpl assemblerOptionsSpecImpl) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec  = AdobeDocAssemblerServiceAdapter.toAdobeAssemblerOptionSpec(assemblerOptionsSpecImpl);
		assertNotEquals(emptyAssemblerOptionSpec.isFailOnError(), adobeAssemblerOptionSpec.isFailOnError());
		
	}

	
}
