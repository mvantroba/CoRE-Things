package de.thk.rdw.rd.resources;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriVariableDefault;

public class GroupResource extends CoapResource {

	private String domain = UriVariableDefault.DOMAIN.toString();

	public GroupResource(String name, String domain) {
		super(name);
		if (domain != null) {
			this.domain = domain;
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		delete();
		exchange.respond(ResponseCode.DELETED);
	}

	public String getDomain() {
		return domain;
	}

	public void updateEndpoints(List<Resource> resources) {
		// Clear old endpoints.
		for (Resource existing : getChildren()) {
			delete(existing);
		}
		// Add new endpoints.
		for (Resource resource : resources) {
			add(resource);
		}
	}
}
