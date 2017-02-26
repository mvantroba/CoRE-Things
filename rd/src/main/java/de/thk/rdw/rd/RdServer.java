package de.thk.rdw.rd;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

import de.thk.rdw.rd.resources.RdResource;

public class RdServer extends CoapServer {

	public RdServer() {
		// Bind endpoints to each network interface.
		for (InetAddress address : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (!address.isLinkLocalAddress()) {
				this.addEndpoint(new CoapEndpoint(new InetSocketAddress(address, CoAP.DEFAULT_COAP_PORT)));
			}
		}
		add(new RdResource());
	}
}
