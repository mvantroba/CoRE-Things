package de.thk.ct.endpoint.server.osgi.network;

import org.eclipse.californium.core.coap.CoAP;

/**
 * Default values for the {@link EndpointConfig}.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointConfigDefaults {

	private EndpointConfigDefaults() {
	}

	/**
	 * Fills an instance of {@link EndpointConfig} with the default values.
	 * 
	 * @param config
	 *            endpoint config to be filled with default values
	 */
	public static void setDefaults(EndpointConfig config) {
		config.setString(EndpointConfig.Keys.RD_SCHEME, CoAP.COAP_URI_SCHEME);
		config.setInt(EndpointConfig.Keys.RD_PORT, CoAP.DEFAULT_COAP_PORT);
		config.setString(EndpointConfig.Keys.RD_HOST, "192.168.0.100");
		config.setString(EndpointConfig.Keys.ENDPOINT_NAME, "node");
		config.setString(EndpointConfig.Keys.ENDPOINT_DOMAIN, "local");
		config.setString(EndpointConfig.Keys.ENDPOINT_TYPE, "default");
		config.setLong(EndpointConfig.Keys.ENDPOINT_LIFETIME, 86400L);
		config.setString(EndpointConfig.Keys.ENDPOINT_CONTEXT, "");
	}
}
