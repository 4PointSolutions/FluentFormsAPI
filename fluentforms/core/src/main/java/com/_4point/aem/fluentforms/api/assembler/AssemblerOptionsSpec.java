package com._4point.aem.fluentforms.api.assembler;

public interface AssemblerOptionsSpec extends AssemblerOptionsSetter{
	
	String	getDefaultStyle();
    
    int	getFirstBatesNumber();
   
    String	getLogLevel();
   
    Boolean	isFailOnError();
   
    Boolean	isTakeOwnership();
   
    Boolean	isValidateOnly();
  
}
