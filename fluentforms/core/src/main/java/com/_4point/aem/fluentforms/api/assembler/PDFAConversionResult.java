package com._4point.aem.fluentforms.api.assembler;

import com._4point.aem.fluentforms.api.Document;

public interface PDFAConversionResult {

    Document getConversionLog();
      
    Document getJobLog();
          
    Document getPDFADocument();
        
    Boolean	isPDFA();
 
 
}
