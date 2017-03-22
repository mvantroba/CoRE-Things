package de.thk.rdw.endpoint.server.osgi;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

public class EndpointServer extends CoapServer {

	public EndpointServer() {
		super();
		// Bind endpoints to each network interface.
		for (InetAddress address : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (!address.isLinkLocalAddress()) {
				this.addEndpoint(new CoapEndpoint(new InetSocketAddress(address, CoAP.DEFAULT_COAP_PORT)));
			}
		}

		CoapResource actuators = new CoapResource(ResourceProfile.ACTUATORS.getName());
		actuators.getAttributes().addResourceType(ResourceProfile.ACTUATORS.getResourceType());

		CoapResource sensors = new CoapResource(ResourceProfile.SENSORS.getName());
		sensors.getAttributes().addResourceType(ResourceProfile.SENSORS.getResourceType());

		CoapResource actuator1 = new CoapResource("1");
		CoapResource led = new CoapResource("led");

		actuator1.add(led);
		actuators.add(actuator1);
		add(actuators, sensors);
	}
}
