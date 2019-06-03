package com._4point.aem.fluentforms.impl;

/**
 * Describes the context in which the service is being used.
 * 
 *   The SERVER_SIDE context is when the API is being used on the server, all validations are in effect.
 *   The CLIENT_SIDE context is when the APU is being used on the client, some validations are disabled because files
 *   may only exist on the server, etc.
 *
 */

public enum UsageContext {
	SERVER_SIDE, CLIENT_SIDE;
}
