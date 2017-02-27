package de.thk.rdw.rd.resources;

import java.util.Map;

import org.eclipse.californium.core.CoapResource;

import de.thk.rdw.rd.uri.UriVariable;

public class EndpointResource extends CoapResource {

	private String domain;
	private String endpointType;
	private Long lifetime;
	private String context;

	public EndpointResource(Map<UriVariable, String> variables) {
		super(variables.get(UriVariable.END_POINT));
		this.domain = variables.get(UriVariable.DOMAIN);
		this.endpointType = variables.get(UriVariable.END_POINT_TYPE);
		this.lifetime = Long.parseLong(variables.get(UriVariable.LIFE_TIME));
		this.context = variables.get(UriVariable.CONTEXT);
	}

	@Override
	public String toString() {
		return " [name=" + getName() + ", domain=" + domain + ", endpointType=" + endpointType + ", lifetime="
				+ lifetime + ", context=" + context + "]";
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEndpointType() {
		return endpointType;
	}

	public void setEndpointType(String endpointType) {
		this.endpointType = endpointType;
	}

	public Long getLifetime() {
		return lifetime;
	}

	public void setLifetime(Long lifetime) {
		this.lifetime = lifetime;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
}
