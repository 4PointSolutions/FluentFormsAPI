package com._4point.aem.fluentforms.impl.generatePDF;

public enum PDFSettings {
	Standard("Standard"),
	High_Quality_Print("High Quality Print"),
	PDFA1b_2005_RGB("PDF/A-1b"),
	PDFA1b_2005_CMY("PDF/A-1b"),
	PDFX1a_2001("PDF/X-1a"),
	Press_Quality("Press Quality"),
	Smallest_File_Size("Smallest File Size");
	private final String pdfSetting;
	
	PDFSettings(String pdfSetting) {
		this.pdfSetting = pdfSetting;
	}

	public String getPdfSetting() {
		return pdfSetting;
	}
	
	
}
