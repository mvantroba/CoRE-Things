package de.thk.rdw.rd.resources;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriVariable;

public class EndpointResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(EndpointResource.class.getName());
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> scheduledFuture;

	private String domain = "local";
	private String endpointType;
	private Long lifetime = 86400L; // 24 Hours
	private String context;

	public EndpointResource(Map<UriVariable, String> variables) {
		super(variables.get(UriVariable.END_POINT));
		if (variables.get(UriVariable.DOMAIN) != null) {
			this.domain = variables.get(UriVariable.DOMAIN);
			// TODO Define attribute names globally.
		}
		getAttributes().addAttribute("d", domain);
		this.endpointType = variables.get(UriVariable.END_POINT_TYPE);
		if (endpointType != null) {
			getAttributes().addAttribute("et", endpointType);
		}
		updateVariables(variables);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// TODO Dont include rd and this resource in the payload.
		String payload = LinkFormat.serializeTree(this);
		exchange.respond(payload);
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		delete();
		exchange.respond(ResponseCode.DELETED);
	}

	@Override
	public synchronized void delete() {
		// Cancel scheduled deletion which would be triggered after the lifetime
		// expires.
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		super.delete();
		LOGGER.log(Level.INFO, "Deleted endpoint: {0}.", new Object[] { this.toString() });
	}

	/**
	 * Updates endpoint parameters after the endpoint sends a registration
	 * update request. Such request can only update lifetime or context
	 * registration parameters. Parameters that have not changed should not be
	 * sent to reduce the size of the message.
	 * <p>
	 * After receiving an update request, resource directory must reset the
	 * timeout and update all received parameters.
	 * <p>
	 * URI Template: /{+location}{?lt,con}
	 * 
	 * @param variables
	 */
	public void updateVariables(Map<UriVariable, String> variables) {
		try {
			this.lifetime = Long.parseLong(variables.get(UriVariable.LIFE_TIME));
		} catch (NumberFormatException e) {
			LOGGER.log(Level.WARNING, "Could not parse lifetime value \"{0}\" to Long. Enforcing default value: {1}.",
					new Object[] { variables.get(UriVariable.LIFE_TIME), lifetime });
		}
		getAttributes().addAttribute("lt", String.valueOf(lifetime));
		updateLifetime(lifetime);
		this.context = variables.get(UriVariable.CONTEXT);
		getAttributes().addAttribute("con", context);
	}

	public void updateResources(String linkFormat) {
		Set<WebLink> links = LinkFormat.parse(linkFormat);
		String resourceName;
		String uri;
		Scanner uriScanner = null;
		CoapResource resource = this;
		CoapResource childResource = null;
		for (WebLink link : links) {
			uri = link.getURI();
			uriScanner = new Scanner(uri).useDelimiter("/");
			while (uriScanner.hasNext()) {
				resourceName = uriScanner.next();
				for (Resource existingChildResource : resource.getChildren()) {
					if (existingChildResource.getName().equals(resourceName)) {
						childResource = (CoapResource) existingChildResource;
					}
				}
				if (childResource == null) {
					childResource = new CoapResource(resourceName);
					resource.add(childResource);
				}
				resource = childResource;
				childResource = null;
			}
			for (String attr : link.getAttributes().getAttributeKeySet()) {
				for (String value : link.getAttributes().getAttributeValues(attr)) {
					resource.getAttributes().addAttribute(attr, value);
				}
			}
			resource = this;
		}
		if (uriScanner != null) {
			uriScanner.close();
		}
	}

	@Override
	public String toString() {
		return " [name=" + getName() + ", domain=" + domain + ", endpointType=" + endpointType + ", lifetime="
				+ lifetime + ", context=" + context + "]";
	}

	private void updateLifetime(Long lifetime) {
		if (lifetime != null) {
			this.lifetime = lifetime;
			if (scheduledFuture != null) {
				scheduledFuture.cancel(true);
			}
			scheduledFuture = SCHEDULER.schedule(new Runnable() {

				@Override
				public void run() {
					delete();
				}
			}, lifetime, TimeUnit.SECONDS);
		}
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
