package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.LogLevel;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec {
	
	private Boolean isFailOnError;
    private String defaultStyle;
    private int firstBatesNumber;
    private LogLevel logLevel;
    private Boolean isTakeOwnerShip;
    private Boolean isValidateOnly;
	
	@Override
	public Boolean isFailOnError() {
		return isFailOnError;
	}
	
	@Override
	public String getDefaultStyle() {		
		return defaultStyle;
	}
	
	@Override
	public int getFirstBatesNumber() {		
		return firstBatesNumber;
	}
	
	@Override
	public LogLevel getLogLevel() {	
		return logLevel;
	}
	
	@Override
	public Boolean isTakeOwnership() {		
		return isTakeOwnerShip;
	}
	
	@Override
	public Boolean isValidateOnly() {	
		return isValidateOnly;
	}
	
	@Override
	public AssemblerOptionsSpecImpl setDefaultStyle(String defaultStyle) {
		 this.defaultStyle = defaultStyle;
		 return this;
	}
	
	@Override
	public AssemblerOptionsSpecImpl setFirstBatesNumber(int firstBatesNumber) {
		this.firstBatesNumber = firstBatesNumber;
		return this;
	}
	
	
	@Override
	public AssemblerOptionsSpecImpl setTakeOwnership(Boolean isTakeOwnerShip) {
		this.isTakeOwnerShip = isTakeOwnerShip;
		return this;
	}
	
	@Override
	public AssemblerOptionsSpecImpl setValidateOnly(Boolean isValidateOnly) {
		this.isValidateOnly = isValidateOnly;
		return this;
	}

	@Override
	public AssemblerOptionsSpecImpl setFailOnError(Boolean isFailOnError) {
		 this.isFailOnError = isFailOnError;
		 return this;
	}

	@Override
	public AssemblerOptionsSetter setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}
	
}
