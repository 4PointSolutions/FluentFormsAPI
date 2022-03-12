package com._4point.aem.docservices.rest_services.cli.restservices.cli;

public class BundlesCommands {

	// This class will contain the commands related to bundles and bundle verions.
	//
	// Anticipated commands are:
	// bundles deploy - dploey the fluentforms.core and rest-services.server bundles to the AEM server
	// bundles version - show the version information for the fluentforms.core and rest-service.server bundles on the AEM server
	// bundles local_version - shows the version information for the local veersions of the bundles.
	//
	// The AEM Shell .jar will store vopies of the server-side bundles in src/main/resources and will deploy them when 
	// instructed to do so.
	//
	// The versions will be copied into src/main/resources as part of the build process/
	// The local versions will be read by reasing in the .jsr files and reasong the version information from a file within
	// the bundle/jar's src/main/resources directory.
	//
	// Need to determine how to include the version information into a file in src/main/resources.  Probably will use
	// maven buildnumber plugin.
	
}
