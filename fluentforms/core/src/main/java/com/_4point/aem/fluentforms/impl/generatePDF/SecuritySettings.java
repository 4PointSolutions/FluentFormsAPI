package com._4point.aem.fluentforms.impl.generatePDF;

public enum SecuritySettings {
	
	No_Security("No Security"),
	Password_Security("Password Security"),
	Certificate_Security("Certificate Security"),
	Adobe_Policy_Server("Adobe Policy Server");

	private final String securitySetting;
	SecuritySettings(String securitySetting) {
	  this.securitySetting = securitySetting;
	}
	
	public String getSecuritySetting() {
		return securitySetting;
	}
	
	
}
