package de.thk.rdw.endpoint.server.osgi;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;

public class EndpointServer extends CoapServer {

	public EndpointServer() {
		super();
		add(new CoapResource("light"));
	}
}
