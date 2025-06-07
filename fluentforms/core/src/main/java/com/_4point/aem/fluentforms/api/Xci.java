package com._4point.aem.fluentforms.api;

public interface Xci {

	public Document toDocument();
	public Document toDocument(DocumentFactory factory);
	
	public interface XciBuilder {
		DestinationBuilder pdf();
		DestinationBuilder pcl();
		DestinationBuilder ps();
		Xci build();
		
		public interface DestinationBuilder {
			DestinationBuilder embedFonts(Boolean embedFonts);
			XciBuilder buildDestination();
		}
		
	}
}
