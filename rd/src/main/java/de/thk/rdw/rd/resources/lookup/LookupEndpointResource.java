package de.thk.rdw.rd.resources.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdGroupResource;
import de.thk.rdw.rd.resources.RdResource;
import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

/**
 * The {@link AbstractLookupResource} type which allows to perform an endpoint
 * lookup. List of endpoints can be retrieved by sending a GET request to this
 * resource.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupEndpointResource extends AbstractLookupResource {

	private RdResource rdResource = null;
	private RdGroupResource rdGroupResource = null;

	public LookupEndpointResource(RdResource rdResource, RdGroupResource rdGroupResource) {
		super(LookupType.ENDPOINT.getType());
		this.rdResource = rdResource;
		this.rdGroupResource = rdGroupResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String resultPayload;
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		List<Resource> endpointResources = new ArrayList<>();

		if (variables.containsKey(UriVariable.GROUP)) {
			// Filter by group.
			for (Resource resource : rdGroupResource.getChildren()) {
				if (resource.getName().equals(variables.get(UriVariable.GROUP))) {
					endpointResources.addAll(resource.getChildren());
				}
			}
		} else if (variables.containsKey(UriVariable.DOMAIN)) {
			// Filter by domain.
			for (Resource resource : rdResource.getChildren()) {
				if (resource instanceof EndpointResource) {
					EndpointResource endpointResource = (EndpointResource) resource;
					if (endpointResource.getDomain().equals(variables.get(UriVariable.DOMAIN))) {
						endpointResources.add(resource);
					}
				}
			}
		} else {
			// All endpoints.
			for (Resource resource : rdResource.getChildren()) {
				if (resource instanceof EndpointResource) {
					endpointResources.add(resource);
				}
			}
		}

		resultPayload = toLinkFormat(endpointResources, variables.get(UriVariable.PAGE),
				variables.get(UriVariable.COUNT));

		if (!resultPayload.isEmpty()) {
			exchange.respond(ResponseCode.CONTENT, resultPayload, MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
