package de.thk.rdw.endpoint.server.osgi.network;

import org.eclipse.californium.core.coap.CoAP;

public class EndpointConfigDefaults {

	private EndpointConfigDefaults() {
	}

	public static void setDefaults(EndpointConfig config) {
		config.setString(EndpointConfig.Keys.RD_SCHEME, CoAP.COAP_URI_SCHEME);
		config.setInt(EndpointConfig.Keys.RD_PORT, CoAP.DEFAULT_COAP_PORT);
		config.setString(EndpointConfig.Keys.RD_HOST, "127.0.0.1");
		config.setString(EndpointConfig.Keys.ENDPOINT_NAME, "node");
		config.setString(EndpointConfig.Keys.ENDPOINT_DOMAIN, "local");
		config.setString(EndpointConfig.Keys.ENDPOINT_TYPE, "default");
		config.setLong(EndpointConfig.Keys.ENDPOINT_LIFETIME, 86400L);
	}
}
