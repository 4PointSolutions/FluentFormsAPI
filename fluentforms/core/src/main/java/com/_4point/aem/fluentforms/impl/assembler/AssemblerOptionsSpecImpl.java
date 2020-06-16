package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec{
	private Boolean isFailOnError;
	@Override
	public AssemblerOptionsSetter setFailOnError(boolean isFailOnError) {
		this.isFailOnError = isFailOnError;
		return this;
	}

	@Override
	public boolean isFailOnError() {
		return isFailOnError;
	}

}
