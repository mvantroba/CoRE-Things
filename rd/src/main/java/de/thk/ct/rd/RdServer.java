package de.thk.ct.rd;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

import de.thk.ct.rd.resources.EndpointResource;
import de.thk.ct.rd.resources.RdGroupResource;
import de.thk.ct.rd.resources.RdLookupResource;
import de.thk.ct.rd.resources.RdResource;

/**
 * The {@link CoapServer} type which implements the CoRE Resource Directory
 * according to the IETF Internet-Draft (Standards Track). In this draft, the
 * Resource Directory is described as a web entity that stores information about
 * web resources and implements the REST interfaces for registration and lookup
 * of those resources.
 * <p>
 * The REST interfaces consists of resources that extends the {@link Resource}
 * class. The most important resource in a resource directory is the
 * {@link RdResource}. It enables other web servers (endpoints) to register
 * themselves and their resources. In this software, endpoint is represented by
 * the {@link EndpointResource}.
 * <p>
 * The {@link RdGroupResource} enables a commisioning tool (CT) registration of
 * endpoint groups. The {@link RdLookupResource} enables to perform various
 * lookups.
 * 
 * @author Martin Vantroba
 * 
 * @see <a href=
 *      "https://tools.ietf.org/html/draft-ietf-core-resource-directory-08">CoRE
 *      Resource Directory</a>
 *
 */
public class RdServer extends CoapServer {

	/**
	 * Constructs a {@link RdServer}, iterates over all available network
	 * interfaces and binds endpoints to each of them. It also initializes and
	 * adds {@link RdResource}, {@link RdGroupResource} and
	 * {@link RdLookupResource} to the server. These resources represent the
	 * REST interfaces which can be accessed by clients.
	 */
	public RdServer() {
		// Bind endpoints to each network interface.
		for (InetAddress address : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (!address.isLinkLocalAddress()) {
				this.addEndpoint(new CoapEndpoint(new InetSocketAddress(address, CoAP.DEFAULT_COAP_PORT)));
			}
		}
		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		add(rdResource);
		add(rdGroupResource);
		add(new RdLookupResource(rdResource, rdGroupResource));
	}
}
