package com._4point.aem.fluentforms.impl;

import java.util.EnumMap;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.Xci;

public class XciImpl implements Xci {
	private static final String XCI_FORMAT_STR = """
			<?xml version="1.0" encoding="UTF-8"?>
			<xdp:xdp xmlns:xdp="http://ns.adobe.com/xdp/">
				<config xmlns="http://www.xfa.org/schema/xci/1.0/"
					xmlns:xfa="http://www.xfa.org/schema/xci/1.0/">
					%s
				</config>
			</xdp:xdp>
			""";

	private static final String XCI_DESTINATION_FORMAT_STR = """
			<destination>%s</destination> <!-- pdf|ps|pcl -->
			<%s> <!-- [0..n] -->
				<fontInfo>
					<embed>%d</embed> <!-- 0|1 -->
				</fontInfo>
			</%s>
			""";

	private enum DestinationType {
		PDF("pdf"), PCL("pcl"), PS("ps");

		private final String name;

		DestinationType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
	private static record Destination(String name, boolean embedFonts) {
        public String toXml() {
            return destinationToXml(name, embedFonts);
        }

        private static String destinationToXml(String destinationName, boolean embedFonts) {
    		return XCI_DESTINATION_FORMAT_STR.formatted(destinationName, destinationName, embedFonts ? 1 : 0, destinationName);
    	}
    };

    private final Map<DestinationType, Destination> destinations;
    
	private XciImpl(Map<DestinationType, Destination> destinations) {
		this.destinations = destinations;
	}

	@Override
	public Document toDocument() {
		return toDocument(SimpleDocumentFactoryImpl.getFactory());
	}

	@Override
	public Document toDocument(DocumentFactory factory) {
		// Implemented with simple String operations for now, eventually move to a proper XML builder.
		StringBuilder destSection = new StringBuilder();
		destinations.values().forEach(dest -> {
				destSection.append( "<present>" + dest.toXml() + "</present>");
			});
		var result = XCI_FORMAT_STR.formatted(destSection.toString());
		return factory.create(result.getBytes());
	}

	public static class JavaxXciBuilder implements Xci.XciBuilder {
		private DestinationBuilderImpl pdfBuilder = null;
		private DestinationBuilderImpl pclBuilder = null;
		private DestinationBuilderImpl psBuilder = null;
		
		@Override
		public DestinationBuilder pdf() {
			return this.pdfBuilder == null ? this.pdfBuilder = new DestinationBuilderImpl() : this.pdfBuilder;
		}

		@Override
		public DestinationBuilder pcl() {
			return this.pclBuilder == null ? this.pclBuilder = new DestinationBuilderImpl() : this.pclBuilder;
		}

		@Override
		public DestinationBuilder ps() {
			return this.psBuilder == null ? this.psBuilder = new DestinationBuilderImpl() : this.psBuilder;
		}

		@Override
		public Xci build() {
			var destinations = new EnumMap<DestinationType, Destination>(DestinationType.class);
			if (pdfBuilder != null && pdfBuilder.embedFonts != null) {
				destinations.put(DestinationType.PDF, new Destination(DestinationType.PDF.getName(), pdfBuilder.embedFonts));
			}
			if (pclBuilder != null && pclBuilder.embedFonts != null) {
				destinations.put(DestinationType.PCL, new Destination(DestinationType.PCL.getName(), pclBuilder.embedFonts));
			}
			if (psBuilder != null && psBuilder.embedFonts != null) {
				destinations.put(DestinationType.PS, new Destination(DestinationType.PS.getName(), psBuilder.embedFonts));
			}
			return new XciImpl(destinations);
		}
		
		private class DestinationBuilderImpl implements DestinationBuilder {

			private Boolean embedFonts = null;

			@Override
			public DestinationBuilder embedFonts(Boolean embedFonts) {
				this.embedFonts = embedFonts;
				return this;
			}

			@Override
			public XciBuilder buildDestination() {
				return JavaxXciBuilder.this;
			}
		}		
	}
}
