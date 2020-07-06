package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec {
	
	private Boolean isFailOnError;
	private PathOrUrl contentRoot;
	
	@Override
	public Boolean isFailOnError() {
		return isFailOnError;
	}
	@Override
	public AssemblerOptionsSpecImpl setFailOnError(Boolean isFailOnError) {
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
	
	@Override
	public String getDefaultStyle() {		
		return null;
	}
	
	@Override
	public int getFirstBatesNumber() {		
		return 0;
	}
	
	@Override
	public String getLogLevel() {
		
		return null;
	}
	
	@Override
	public Boolean isTakeOwnership() {		
		return false;
	}
	
	@Override
	public Boolean isValidateOnly() {
		
		return false;
	}
	
	@Override
	public AssemblerOptionsSpec setDefaultStyle(String defaultStyle) {
		
		return null;
	}
	
	@Override
	public AssemblerOptionsSpec setFirstBatesNumber(int start) {
		
		return null;
	}
	
	@Override
	public AssemblerOptionsSpec setLogLevel(String logLevel) {
		
		return null;
	}
	
	@Override
	public AssemblerOptionsSpec setTakeOwnership(Boolean takeOwnership) {
		
		return null;
	}
	
	@Override
	public AssemblerOptionsSpec setValidateOnly(Boolean validateOnly) {
		
		return null;
	}
	

	
	


}
