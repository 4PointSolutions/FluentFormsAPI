package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec{
	private Boolean isFailOnError;
	private PathOrUrl contentRoot;
	
	
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
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getFirstBatesNumber() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getLogLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssemblerOptionsSpec setDefaultStyle(String defaultStyle) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AssemblerOptionsSpec setFirstBatesNumber(int start) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AssemblerOptionsSpec setLogLevel(String logLevel) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boolean isFailOnError() {
		// TODO Auto-generated method stub
		return isFailOnError;
	}
	@Override
	public Boolean isTakeOwnership() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean isValidateOnly() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AssemblerOptionsSetter setTakeOwnership(Boolean takeOwnership) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AssemblerOptionsSetter setValidateOnly(Boolean validateOnly) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	


}
