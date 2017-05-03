package de.thk.ct.rd.resources.lookup;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.base.RdLookupType;
import de.thk.ct.rd.resources.EndpointResource;
import de.thk.ct.rd.resources.RdResource;

/**
 * The {@link LookupEndpointResource} type which allows to perform an endpoint
 * lookup. List of endpoints can be retrieved by sending a GET request to this
 * resource.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupEndpointResource extends CoapResource {

	private RdResource rdResource = null;

	/**
	 * Creates a {@link LookupEndpointResource} with name according to the
	 * {@link LinkFormat} and initializes the {@link RdResource} which is needed
	 * to retrieve list of endpoints.
	 * 
	 * @param rdResource
	 *            Resource to which endpoints register themselves.
	 */
	public LookupEndpointResource(RdResource rdResource) {
		super(RdLookupType.ENDPOINT.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		StringBuilder resultPayload = new StringBuilder();

		for (Resource resource : rdResource.getChildren()) {
			if (resource instanceof EndpointResource) {
				resultPayload.append(LinkFormat.serializeResource(resource));
			}
		}

		if (resultPayload.length() > 0) {
			exchange.respond(ResponseCode.CONTENT, resultPayload.toString(), MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
