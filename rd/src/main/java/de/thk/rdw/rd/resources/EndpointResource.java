package de.thk.rdw.rd.resources;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;

import de.thk.rdw.rd.uri.UriVariable;

public class EndpointResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(EndpointResource.class.getName());

	private String domain = "local";
	private String endpointType;
	private Long lifetime = 86400L; // 24 Hours
	private String context;

	public EndpointResource(Map<UriVariable, String> variables) {
		super(variables.get(UriVariable.END_POINT));
		if (variables.get(UriVariable.DOMAIN) != null) {
			this.domain = variables.get(UriVariable.DOMAIN);
		}
		updateVariables(variables);
	}

	public void updateVariables(Map<UriVariable, String> variables) {
		try {
			this.lifetime = Long.parseLong(variables.get(UriVariable.LIFE_TIME));
		} catch (NumberFormatException e) {
			LOGGER.log(Level.WARNING, "Could not parse lifetime value \"{0}\" to Long. Enforcing default value: {1}.",
					new Object[] { variables.get(UriVariable.LIFE_TIME), lifetime });
		}
		this.endpointType = variables.get(UriVariable.END_POINT_TYPE);
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

	public String getEndpointType() {
		return endpointType;
	}

	public Long getLifetime() {
		return lifetime;
	}

	public String getContext() {
		return context;
	}
}
