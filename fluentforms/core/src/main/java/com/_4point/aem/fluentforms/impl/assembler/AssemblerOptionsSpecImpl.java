package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec{
	private Boolean isFailOnError;
	private PathOrUrl contentRoot;
	
	@Override
	public boolean isFailOnError() {
		return isFailOnError;
	}
	@Override
	public AssemblerOptionsSpecImpl setFailOnError(boolean isFailOnError) {
		 this.isFailOnError = isFailOnError;
		 return this;
	}
	@Override
	public AssemblerOptionsSetter setContentRoot(PathOrUrl contentRoot) {
		this.contentRoot = contentRoot;
		return this;
	}
	public PathOrUrl getContentRoot() {
		return contentRoot;
	}
	

	
	


}
