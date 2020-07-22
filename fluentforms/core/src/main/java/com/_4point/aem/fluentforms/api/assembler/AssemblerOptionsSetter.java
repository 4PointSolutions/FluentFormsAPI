package com._4point.aem.fluentforms.api.assembler;

import com._4point.aem.fluentforms.impl.assembler.LogLevel;

public interface AssemblerOptionsSetter {
	
	AssemblerOptionsSetter setFailOnError (Boolean isFailOnError);

	AssemblerOptionsSetter setDefaultStyle(String defaultStyle);
    
	AssemblerOptionsSetter setFirstBatesNumber(int start);
    
	AssemblerOptionsSetter setLogLevel(LogLevel logLevel);
   
	AssemblerOptionsSetter setTakeOwnership(Boolean takeOwnership);
    
	AssemblerOptionsSetter setValidateOnly(Boolean validateOnly);

}
