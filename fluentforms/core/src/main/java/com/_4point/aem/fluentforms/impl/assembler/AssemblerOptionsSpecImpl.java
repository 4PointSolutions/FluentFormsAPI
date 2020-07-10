package com._4point.aem.fluentforms.impl.assembler;

import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;

public class AssemblerOptionsSpecImpl implements AssemblerOptionsSpec {
	
	private Boolean isFailOnError;
    private String defaultStyle;
    private int firstBatesNumber;
    private String logLevel;
    private Boolean isTakeOwnerShip;
    private Boolean isValidateOnly;
	
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
	public String getDefaultStyle() {		
		return defaultStyle;
	}
	
	@Override
	public int getFirstBatesNumber() {		
		return firstBatesNumber;
	}
	
	@Override
	public String getLogLevel() {
		for(LogLevel l : LogLevel.values()){
			logLevel = l.toString();
		}
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
	public AssemblerOptionsSpecImpl setLogLevel(String logLevel) {
		this.logLevel = logLevel;
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

}
