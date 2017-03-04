package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.rdw.rd.uri.UriVariable;
import de.thk.rdw.rd.uri.UriVariableDefault;

public class GroupResource extends CoapResource {

	private String domain = String.valueOf(UriVariableDefault.DOMAIN.getDefaultValue());

	public GroupResource(String name, String domain) {
		super(name);
		if (domain != null) {
			UriVariable.DOMAIN.validate(domain);
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
}
