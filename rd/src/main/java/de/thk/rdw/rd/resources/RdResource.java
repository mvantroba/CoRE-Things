package de.thk.rdw.rd.resources;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.base.RdResourceType;
import de.thk.rdw.rd.resources.lookup.LookupEndpointResource;
import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

/**
 * The {@link RdResource} type which implements function set for the most
 * important resource in a Resource Directory. This resource allows other web
 * servers (endpoints) to register themselves and their resources in Resource
 * Directory. These endpoints can be then obtained by using lookup interfaces of
 * Resource Directory, such as {@link LookupEndpointResource}.
 * <p>
 * This resource is observable which allows clients to send observe requests to
 * this resource in order to be notified of any changes.
 * 
 * @author Martin Vantroba
 *
 */
public class RdResource extends CoapResource {

	private static final Logger RD_LOGGER = Logger.getLogger(RdResource.class.getName());

	/**
	 * Constructs a {@link RdResource}, initializes its resource type and
	 * enables observation.
	 */
	public RdResource() {
		super(RdResourceType.CORE_RD.getName());
		getAttributes().addResourceType(RdResourceType.CORE_RD.getType());
		setObservable(true);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		Response response = new Response(ResponseCode.CONTENT);
		response.setPayload(LinkFormat.serializeTree(this));
		exchange.respond(response);
		if (exchange.getRequestOptions().hasObserve()) {
			ObservingEndpoint observingEndpoint = new ObservingEndpoint(
					new InetSocketAddress(exchange.getSourceAddress(), exchange.getSourcePort()));
			// TODO This will cause endpoint to be added to observers multiple
			// times, because it hasObserve() will return true.
			addObserveRelation(new ObserveRelation(observingEndpoint, this, exchange.advanced()));
			RD_LOGGER.log(Level.INFO, "Added endpoint to observers: {0}", observingEndpoint.getAddress().toString());
		}
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		RD_LOGGER.log(Level.INFO, "Registration request from {0}:{1}.",
				new Object[] { exchange.getSourceAddress().getHostAddress(), exchange.getSourcePort() });
		exchange.respond(preparePostResponse(exchange));
		changed();
	}

	/**
	 * Searches a registered endpoint resource which has the same name as the
	 * method parameter and is associated with the same domain as the method
	 * parameter.
	 * 
	 * @param endpointResource
	 *            searched endpoint
	 * @return found endpoint, null if not found
	 */
	public EndpointResource findChildEndpointResource(EndpointResource endpointResource) {
		return findChildEndpointResource(endpointResource.getName(), endpointResource.getDomain());
	}

	/**
	 * Searches a registered endpoint resource which has the same name as the
	 * method parameter and is associated with the same domain as the method
	 * parameter.
	 * 
	 * @param name
	 *            name of the searched endpoint
	 * @param domain
	 *            domain the searched endpoint is associated with
	 * @return found endpoint, null if not found
	 */
	public EndpointResource findChildEndpointResource(String name, String domain) {
		EndpointResource result = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((EndpointResource) child).getDomain();
			if (childName != null && childName.equals(name) && childDomain != null && childDomain.equals(domain)) {
				result = (EndpointResource) child;
				break;
			}
		}
		return result;
	}

	private Response preparePostResponse(CoapExchange exchange) {
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		variables.put(UriVariable.CONTEXT,
				initContextFromRequest(exchange.advanced().getRequest(), variables.get(UriVariable.CONTEXT)));
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
				newEndpoint.updateResources(exchange.advanced().getRequest().getPayloadString());
				response = new Response(ResponseCode.CREATED);
				exchange.setLocationPath(newEndpoint.getURI());
				RD_LOGGER.log(Level.INFO, "Added new endpoint: {0}.", new Object[] { newEndpoint.toString() });
			} else {
				existingEndpoint.updateVariables(variables.get(UriVariable.LIFE_TIME),
						variables.get(UriVariable.CONTEXT));
				existingEndpoint.updateResources(exchange.advanced().getRequest().getPayloadString());
				response = new Response(ResponseCode.CHANGED);
				exchange.setLocationPath(existingEndpoint.getURI());
				RD_LOGGER.log(Level.INFO, "Updated endpoint: {0}.", new Object[] { existingEndpoint.toString() });
			}
		} else {
			response = new Response(ResponseCode.BAD_REQUEST);
			response.setPayload(String.format("Uri variable \"%s\" is mandatory.", UriVariable.END_POINT));
		}
		return response;
	}

	private String initContextFromRequest(Request request, String context) {
		String result = null;
		String scheme = null;
		String host = null;
		int port = -1;
		URI uri;
		if (context != null) {
			try {
				// Uri at which the server is available.
				uri = new URI(context);
				scheme = uri.getScheme();
				host = uri.getHost();
				port = uri.getPort();
			} catch (URISyntaxException e) {
				RD_LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		// If the context parameter is not in the request, source scheme, host
		// and port are assumed.
		scheme = scheme != null ? scheme : getSchemeFromRequest(request);
		host = host != null ? host : request.getSource().getHostAddress();
		port = port > 0 ? port : request.getSourcePort();
		try {
			result = new URI(scheme, null, host, port, null, null, null).toString();
		} catch (URISyntaxException e) {
			RD_LOGGER.log(Level.WARNING, e.getMessage());
		}
		return result;
	}

	private String getSchemeFromRequest(Request request) {
		String result = request.getScheme();
		if (result == null || result.isEmpty()) {
			if (request.getOptions().getUriPort() != null
					&& request.getOptions().getUriPort().intValue() == CoAP.DEFAULT_COAP_SECURE_PORT) {
				result = CoAP.COAP_SECURE_URI_SCHEME;
			} else {
				result = CoAP.COAP_URI_SCHEME;
			}
		}
		return result;
	}
}
