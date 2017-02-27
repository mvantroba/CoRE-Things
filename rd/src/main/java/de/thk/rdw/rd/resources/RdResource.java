package de.thk.rdw.rd.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

public class RdResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(RdResource.class.getName());

	private static final String DEFAULT_DOMAIN = "local";
	private static final String DEFAULT_LIFE_TIME = "86400"; // 24 Hours

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
		Map<UriVariable, String> variables = initVariablesFromExchange(exchange);
		EndpointResource newEndpoint;
		EndpointResource existingEndpoint;
		Response response;
		// Check if query contains mandatory variable endpoint name.
		if (variables.containsKey(UriVariable.END_POINT)) {
			newEndpoint = new EndpointResource(variables);
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
		} else {
			response = new Response(ResponseCode.BAD_REQUEST);
			response.setPayload(String.format("Uri variable \"%s\" is mandatory.", UriVariable.END_POINT));
		}
		return response;
	}

	private Map<UriVariable, String> initVariablesFromExchange(CoapExchange exchange) {
		Map<UriVariable, String> result = new EnumMap<>(UriVariable.class);
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		String endpoint = variables.get(UriVariable.END_POINT);
		String domain = variables.get(UriVariable.DOMAIN);
		String endpointType = variables.get(UriVariable.END_POINT_TYPE);
		String lifetime = variables.get(UriVariable.LIFE_TIME);
		if (endpoint != null) {
			try {
				UriVariable.END_POINT.validate(endpoint);
				result.put(UriVariable.END_POINT, endpoint);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		if (domain != null) {
			try {
				UriVariable.DOMAIN.validate(domain);
				result.put(UriVariable.DOMAIN, domain);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
				result.put(UriVariable.DOMAIN, DEFAULT_DOMAIN);
			}
		} else {
			result.put(UriVariable.DOMAIN, DEFAULT_DOMAIN);
		}
		if (endpointType != null) {
			try {
				UriVariable.END_POINT_TYPE.validate(endpointType);
				result.put(UriVariable.END_POINT_TYPE, endpointType);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		if (lifetime != null) {
			try {
				UriVariable.LIFE_TIME.validate(lifetime);
				result.put(UriVariable.LIFE_TIME, lifetime);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
				result.put(UriVariable.LIFE_TIME, DEFAULT_LIFE_TIME);
			}
		} else {
			result.put(UriVariable.LIFE_TIME, DEFAULT_LIFE_TIME);
		}
		result.put(UriVariable.CONTEXT,
				initContextFromRequest(exchange.advanced().getRequest(), variables.get(UriVariable.CONTEXT)));
		return result;
	}

	private String initContextFromRequest(Request request, String context) {
		String result = null;
		String scheme = null;
		String host = null;
		int port = -1;
		if (context != null) {
			try {
				URI uri = new URI(context);
				scheme = uri.getScheme();
				host = uri.getHost();
				port = uri.getPort();
			} catch (URISyntaxException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		if (scheme == null || scheme.isEmpty()) {
			scheme = request.getScheme();
			if (scheme == null || scheme.isEmpty()) {
				if (request.getOptions().getUriPort() != null
						&& request.getOptions().getUriPort().intValue() == CoAP.DEFAULT_COAP_SECURE_PORT) {
					scheme = CoAP.COAP_SECURE_URI_SCHEME;
				} else {
					scheme = CoAP.COAP_URI_SCHEME;
				}
			}
		}
		if (host == null) {
			host = request.getSource().getHostAddress();
		}
		if (port < 0) {
			port = request.getSourcePort();
		}
		try {
			result = new URI(scheme, null, host, port, null, null, null).toString();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		}
		return result;
	}

	private EndpointResource findChildEndpointResource(EndpointResource endpointResource) {
		EndpointResource result = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((EndpointResource) child).getDomain();
			if (childName != null && childName.equals(endpointResource.getName()) && childDomain != null
					&& childDomain.equals(endpointResource.getDomain())) {
				result = (EndpointResource) child;
				break;
			}
		}
		return result;
	}
}
