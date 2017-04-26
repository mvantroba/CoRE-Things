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

import de.thk.rdw.rd.uri.UriVariable;
import de.thk.rdw.rd.uri.UriVariableDefault;

public class EndpointResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(EndpointResource.class.getName());
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	private String domain = UriVariableDefault.DOMAIN.toString();
	private String endpointType;
	private Long lifetime = Long.parseLong(UriVariableDefault.LIFE_TIME.toString());
	private String context;

	private ScheduledFuture<?> scheduledFuture;

	public EndpointResource(String name, String domain, String endpointType, String lifetime, String context) {
		super(name);
		getAttributes().addAttribute(LinkFormat.END_POINT, name);
		if (domain != null) {
			this.domain = domain;
		}
		getAttributes().addAttribute(LinkFormat.DOMAIN, this.domain);
		this.endpointType = endpointType;
		if (this.endpointType != null) {
			getAttributes().addAttribute(LinkFormat.END_POINT_TYPE, this.endpointType);
		}
		try {
			this.lifetime = Long.parseLong(lifetime);
		} catch (NumberFormatException e) {
			// Default value will be used.
		}
		getAttributes().addAttribute(LinkFormat.LIFE_TIME, String.valueOf(this.lifetime));
		updateScheduledFuture();
		this.context = context;
		if (this.context != null) {
			getAttributes().addAttribute(LinkFormat.CONTEXT, this.context);
		}
	}

	public EndpointResource(Map<UriVariable, String> variables) {
		this(variables.get(UriVariable.END_POINT), //
				variables.get(UriVariable.DOMAIN), //
				variables.get(UriVariable.END_POINT_TYPE), //
				variables.get(UriVariable.LIFE_TIME), //
				variables.get(UriVariable.CONTEXT));
	}

	public EndpointResource(String name, String domain) {
		this(name, domain, null, null, null);
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
	 * <p>
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
	public void updateVariables(String lifetime, String context) {
		try {
			this.lifetime = Long.parseLong(lifetime);
		} catch (NumberFormatException e) {
			// Old value will be used.
		}
		// Remove previous entry to prevent having multiple values.
		getAttributes().clearAttribute(LinkFormat.LIFE_TIME);
		getAttributes().addAttribute(LinkFormat.LIFE_TIME, String.valueOf(this.lifetime));
		updateScheduledFuture();

		if (context != null) {
			this.context = context;
			getAttributes().clearAttribute(LinkFormat.CONTEXT);
			getAttributes().addAttribute(LinkFormat.CONTEXT, this.context);
		}
	}

	public void updateResources(String linkFormat) {
		Set<WebLink> links = LinkFormat.parse(linkFormat);
		String resourceName;
		String uri;
		Scanner uriScanner = null;
		CoapResource resource = this;
		CoapResource childResource = null;
		getChildren().clear();
		for (WebLink link : links) {
			uri = link.getURI();
			uriScanner = new Scanner(uri).useDelimiter("/");
			while (uriScanner.hasNext()) {
				resourceName = uriScanner.next();
				childResource = new CoapResource(resourceName);
				resource.add(childResource);
				resource = childResource;
				childResource = null;
			}
			for (String attr : link.getAttributes().getAttributeKeySet()) {
				for (String value : link.getAttributes().getAttributeValues(attr)) {
					resource.getAttributes().addAttribute(attr, value);
				}
			}
			resource = this;
			uriScanner.close();
		}
	}

	@Override
	public String toString() {
		return " [name=" + getName() + ", domain=" + domain + ", endpointType=" + endpointType + ", lifetime="
				+ lifetime + ", context=" + context + "]";
	}

	private void updateScheduledFuture() {
		if (lifetime != null) {
			if (scheduledFuture != null) {
				scheduledFuture.cancel(true);
			}
			scheduledFuture = SCHEDULER.schedule(new Runnable() {

				@Override
				public void run() {
					LOGGER.log(Level.INFO, "Endpoint lifetime has expired: {0}",
							new Object[] { EndpointResource.this.toString() });
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
