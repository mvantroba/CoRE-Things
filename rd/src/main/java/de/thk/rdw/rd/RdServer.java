package de.thk.rdw.rd;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RdServer extends CoapServer {

	public RdServer() {
		add(new CoapResource("test") {

			@Override
			public void handleGET(CoapExchange exchange) {
				exchange.respond("Hello World!");
			}
		});
	}
}