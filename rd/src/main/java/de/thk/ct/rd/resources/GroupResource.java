package de.thk.ct.rd.resources;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.rd.uri.UriVariableDefault;

/**
 * The {@link CoapResource} type which represents a group of endpoints
 * represented by the {@link EndpointResource}. It is identified by name which
 * is used during registration, and is unique within the associated domain.
 * 
 * @author Martin Vantroba
 *
 */
public class GroupResource extends CoapResource {

	private String domain = UriVariableDefault.DOMAIN.toString();

	/**
	 * Constructs {@link GroupResource} with the given name and associates it
	 * with the given domain.
	 * 
	 * @param name
	 *            group name
	 * @param domain
	 *            domain this group is associated with
	 */
	public GroupResource(String name, String domain) {
		super(name);
		getAttributes().addAttribute(LinkFormat.END_POINT, name);
		if (domain != null) {
			this.domain = domain;
		}
		getAttributes().addAttribute(LinkFormat.DOMAIN, this.domain);
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		delete();
		exchange.respond(ResponseCode.DELETED);
	}

	/**
	 * Updates endpoints which are members of this group. This method can be
	 * used by {@link RdResource} upon receiving a registration update request.
	 * Old endpoints in this group will be deleted.
	 * 
	 * @param resources
	 *            list of endpoints to be registered in this group
	 */
	public void updateEndpoints(List<Resource> resources) {
		// Clear old endpoints.
		getChildren().clear();
		// Add new endpoints.
		for (Resource resource : resources) {
			add(resource);
		}
	}

	public String getDomain() {
		return domain;
	}
}
