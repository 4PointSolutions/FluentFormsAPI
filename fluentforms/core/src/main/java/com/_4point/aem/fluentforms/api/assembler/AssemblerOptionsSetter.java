package com._4point.aem.fluentforms.api.assembler;

public interface AssemblerOptionsSetter {
	
	AssemblerOptionsSetter setFailOnError (Boolean isFailOnError);

	AssemblerOptionsSetter setDefaultStyle(String defaultStyle);
    
	AssemblerOptionsSetter setFirstBatesNumber(int start);
    
	AssemblerOptionsSetter setLogLevel(LogLevel logLevel);
   
	AssemblerOptionsSetter setTakeOwnership(Boolean takeOwnership);
    
	AssemblerOptionsSetter setValidateOnly(Boolean validateOnly);

}
