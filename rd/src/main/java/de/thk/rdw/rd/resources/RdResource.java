package de.thk.rdw.rd.resources;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

public class RdResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(RdResource.class.getName());

	private static final String DEFAULT_DOMAIN = "local";

	public RdResource() {
		super(UriVariable.RD.getName());
		// TODO Research if it is necessary to add "core.rd" resource type to
		// this resource.
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		LOGGER.log(Level.INFO, "Registration request from {0}:{1}.",
				new Object[] { exchange.getSourceAddress().getHostAddress(), exchange.getSourcePort() });
		exchange.respond(preparePostResponse(exchange));
	}

	private Response preparePostResponse(CoapExchange exchange) {
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		EndpointResource newEndpoint;
		EndpointResource existingEndpoint;
		Response response;
		// Check if query contains mandatory variable endpoint name.
		if (variables.containsKey(UriVariable.END_POINT)) {
			if (!variables.containsKey(UriVariable.DOMAIN)) {
				variables.put(UriVariable.DOMAIN, DEFAULT_DOMAIN);
			}
			try {
				newEndpoint = new EndpointResource(variables.get(UriVariable.END_POINT),
						variables.get(UriVariable.DOMAIN));
				existingEndpoint = findChildEndpointResource(newEndpoint);
				// Check if the endpoint is already registered.
				if (existingEndpoint == null) {
					add(newEndpoint);
					LOGGER.log(Level.INFO, "Added new endpoint: {0}.", new Object[] { newEndpoint.toString() });
					response = new Response(ResponseCode.CREATED);
					exchange.setLocationPath(newEndpoint.getURI());
				} else {
					response = new Response(ResponseCode.CHANGED);
					exchange.setLocationPath(existingEndpoint.getURI());
				}
				// TODO Set resource parameters.
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, "Could not create endpoint. Reason: {0}", new Object[] { e.getMessage() });
				response = new Response(ResponseCode.BAD_REQUEST);
				response.setPayload(e.getMessage());
			}
		} else {
			response = new Response(ResponseCode.BAD_REQUEST);
			response.setPayload(String.format("Uri variable \"%s\" is mandatory.", UriVariable.END_POINT));
		}
		return response;
	}

	private EndpointResource findChildEndpointResource(EndpointResource endpointResource) {
		EndpointResource resource = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((EndpointResource) child).getDomain();
			if (childName != null && childName.equals(endpointResource.getName()) && childDomain != null
					&& childDomain.equals(endpointResource.getDomain())) {
				resource = (EndpointResource) child;
			}
		}
		return resource;
	}
}