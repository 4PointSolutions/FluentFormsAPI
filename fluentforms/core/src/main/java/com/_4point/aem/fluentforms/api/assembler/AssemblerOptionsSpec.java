package com._4point.aem.fluentforms.api.assembler;

import com._4point.aem.fluentforms.impl.assembler.LogLevel;

public interface AssemblerOptionsSpec extends AssemblerOptionsSetter{
	
	String	getDefaultStyle();
    
    int	getFirstBatesNumber();
   
    LogLevel getLogLevel();
   
    Boolean	isFailOnError();
   
    Boolean	isTakeOwnership();
   
    Boolean	isValidateOnly();
  
}
