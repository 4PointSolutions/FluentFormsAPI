package com._4point.aem.fluentforms.api.assembler;

import com._4point.aem.fluentforms.api.Document;

public interface PDFAValidationResult {
	 Document getJobLog();
    
     Document getValidationLog();
  
     boolean isPDFA();

}
