package de.thk.ct.rd.resources.lookup;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.rd.resources.EndpointResource;
import de.thk.ct.rd.resources.RdResource;

/**
 * The {@link CoapResource} type which allows to perform a resource lookup. List
 * of resources can be retrieved by sending a GET request to this resource. The
 * {@link #handleGET(CoapExchange)} method iterates over all endpoints
 * registered on the {@link RdResource} and serializes their resources.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupResourceResource extends CoapResource {

	private RdResource rdResource = null;

	/**
	 * Creates a {@link LookupResourceResource} with name according to the
	 * {@link LinkFormat} and initializes the {@link RdResource} which is needed
	 * to retrieve list of endpoints and their resources.
	 * 
	 * @param rdResource
	 *            Resource to which endpoints can be registered
	 */
	public LookupResourceResource(RdResource rdResource) {
		super(RdLookupType.RESOURCE.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		StringBuilder resultPayload = new StringBuilder();
		String resultString = "";

		for (Resource resource : rdResource.getChildren()) {
			if (resource instanceof EndpointResource) {
				String endpointResources = LinkFormat.serializeTree(resource);
				if (!endpointResources.isEmpty()) {
					resultPayload.append(LinkFormat.serializeTree(resource));
					resultPayload.append(",");
				}
			}
		}

		if (resultPayload.length() > 0) {
			// Remove trailing comma.
			resultString = resultPayload.substring(0, resultPayload.length() - 1);
		}

		if (!resultString.isEmpty()) {
			exchange.respond(ResponseCode.CONTENT, resultString, MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
