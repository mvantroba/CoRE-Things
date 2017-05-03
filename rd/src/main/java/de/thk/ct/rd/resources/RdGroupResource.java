package de.thk.ct.rd.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.base.RdResourceType;
import de.thk.ct.rd.uri.UriUtils;
import de.thk.ct.rd.uri.UriVariable;
import de.thk.ct.rd.uri.UriVariableDefault;

/**
 * The {@link CoapResource} type which allows to register group of endpoints.
 * The {@link #handlePOST(CoapExchange)} allows to register a group in certain
 * domain. Group endpoints are obtained from the message payload. If one or more
 * endpoints in request are not registered in the {@link RdResource}, the group
 * cannot be created.
 * 
 * @author Martin Vantroba
 *
 */
public class RdGroupResource extends CoapResource {

	private RdResource rdResource = null;

	/**
	 * Creates a {@link RdGroupResource} with name according to the
	 * {@link LinkFormat} and initializes the {@link RdResource} which is needed
	 * to retrieve list of endpoints.
	 * 
	 * @param rdResource
	 *            Resource to which endpoints register themselves.
	 */
	public RdGroupResource(RdResource rdResource) {
		super(RdResourceType.CORE_RD_GROUP.getName());
		getAttributes().addResourceType(RdResourceType.CORE_RD_GROUP.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		Response response;
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		String domain = variables.get(UriVariable.DOMAIN);
		List<Resource> endpoints;
		GroupResource existingGroup;
		GroupResource newGroup;
		// Check if the query contains mandatory variable "group name".
		if (variables.containsKey(UriVariable.GROUP)) {
			if (domain == null) {
				domain = UriVariableDefault.DOMAIN.toString();
			}
			existingGroup = findGroupResource(variables.get(UriVariable.GROUP), domain);
			try {
				// Check if all endpoints in request are registered in this
				// domain. If one or more of them are not registered,
				// IllegalArgumentException is thrown.
				endpoints = getEndpointsFromPayload(exchange.advanced().getRequest().getPayloadString(), domain);
				if (existingGroup == null) {
					newGroup = new GroupResource(variables.get(UriVariable.GROUP), domain);
					newGroup.updateEndpoints(endpoints);
					add(newGroup);
					response = new Response(ResponseCode.CREATED);
				} else {
					// Update endpoints if the group already exists.
					existingGroup.updateEndpoints(endpoints);
					response = new Response(ResponseCode.CHANGED);
				}
			} catch (IllegalArgumentException e) {
				// One or more endpoints in the request are not registered so
				// group cannot be created.
				response = new Response(ResponseCode.NOT_FOUND);
			}
		} else {
			response = new Response(ResponseCode.BAD_REQUEST);
		}
		exchange.respond(response);
	}

	private GroupResource findGroupResource(String name, String domain) {
		GroupResource result = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((GroupResource) child).getDomain();
			if (childName != null && childName.equals(name) && childDomain != null && childDomain.equals(domain)) {
				result = (GroupResource) child;
				break;
			}
		}
		return result;
	}

	private List<Resource> getEndpointsFromPayload(String payloadString, String groupDomain) {
		List<Resource> result = new ArrayList<>();
		Set<WebLink> links = LinkFormat.parse(payloadString);
		CoapResource resource = this;
		EndpointResource existingEndpoint;
		String endpointName;

		for (WebLink link : links) {
			if (link.getAttributes().containsAttribute(LinkFormat.END_POINT)) {
				endpointName = link.getAttributes().getAttributeValues(LinkFormat.END_POINT).get(0);
				// Check if endpoint is registered.
				existingEndpoint = rdResource.findChildEndpointResource(endpointName, groupDomain);
				if (existingEndpoint != null) {
					for (String attr : link.getAttributes().getAttributeKeySet()) {
						// Clear old attribute values.
						existingEndpoint.getAttributes().clearAttribute(attr);
						// Add new attribute values.
						for (String value : link.getAttributes().getAttributeValues(attr)) {
							resource.getAttributes().addAttribute(attr, value);
						}
					}
				} else {
					throw new IllegalArgumentException("Endpoint is not registered in the Resource Directory.");
				}
				result.add(existingEndpoint);
			}
		}
		return result;
	}
}
