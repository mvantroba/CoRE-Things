package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;

import de.thk.rdw.rd.uri.UriVariable;

public class EndpointResource extends CoapResource {

	private String domain;

	public EndpointResource(String name, String domain) {
		super(name);
		UriVariable.END_POINT.validate(name);
		UriVariable.DOMAIN.validate(domain);
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "[name=" + getName() + ", domain=" + domain + "]";
	}

	public String getDomain() {
		return domain;
	}
}