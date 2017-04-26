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

import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;
import de.thk.rdw.rd.uri.UriVariableDefault;

/**
 * The {@link CoapResource} type which represents an endpoint. Endpoint is a web
 * server which registers resources to the Resource Directory. It is identified
 * by name which is used during registration, and is unique within the
 * associated domain.
 * <p>
 * An endpoint is characterized by additional attributes. Endpoint type (et)
 * defines the semantic type of the endpoint. Lifetime (lt) defines lifetime of
 * the registration in seconds. After lifetime expires, {@link EndpointResource}
 * deletes itself from the parent, which usually is the {@link RdResource}.
 * <p>
 * The context (con) attribute sets the scheme, address and port at which ths
 * endpoint is available. In the absence of this attribute the context of the
 * register request should be assumed.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointResource extends CoapResource {

	private static final Logger ENDPOINT_LOGGER = Logger.getLogger(EndpointResource.class.getName());
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	private String domain = UriVariableDefault.DOMAIN.toString();
	private String endpointType;
	private Long lifetime = (Long) UriVariableDefault.LIFE_TIME.getDefaultValue();
	private String context;

	private ScheduledFuture<?> scheduledFuture;

	/**
	 * Constructs a {@link EndpointResource} with the complete set of
	 * attributes. It schedules a {@link ScheduledExecutorService} which is
	 * responsible for deleting the endpoint after its lifetime expires.
	 * 
	 * @param name
	 *            endpoint name
	 * @param domain
	 *            domain this endpoint is associated with
	 * @param endpointType
	 *            semantic type of this endpoint
	 * @param lifetime
	 *            lifetime of this endpoint
	 * @param context
	 *            scheme, host and port at which the endpoint is available
	 */
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

	/**
	 * Constructs a {@link EndpointResource} with set of registration variables.
	 * These variables are represented by a {@link Map} of variables of type
	 * {@link UriVariable}. Such map can be obtained by calling the
	 * {@link UriUtils #parseUriQuery(java.util.List)} method.
	 * 
	 * @param variables
	 *            endpoint registration variables
	 */
	public EndpointResource(Map<UriVariable, String> variables) {
		this(variables.get(UriVariable.END_POINT), //
				variables.get(UriVariable.DOMAIN), //
				variables.get(UriVariable.END_POINT_TYPE), //
				variables.get(UriVariable.LIFE_TIME), //
				variables.get(UriVariable.CONTEXT));
	}

	/**
	 * Constructs {@link EndpointResource} with the given name and associates it
	 * with the given domain. Other attributes will be either set to default
	 * values or to null.
	 * 
	 * @param name
	 *            endpoint name
	 * @param domain
	 *            domain this endpoint is associated with
	 */
	public EndpointResource(String name, String domain) {
		this(name, domain, null, null, null);
	}

	@Override
	public String toString() {
		return " [name=" + getName() + ", domain=" + domain + ", endpointType=" + endpointType + ", lifetime="
				+ lifetime + ", context=" + context + "]";
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(LinkFormat.serializeTree(this));
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
		ENDPOINT_LOGGER.log(Level.INFO, "Deleted endpoint: {0}.", new Object[] { this.toString() });
	}

	/**
	 * <p>
	 * Updates endpoint parameters after the endpoint sends a registration
	 * update request. Such request can only update lifetime and context
	 * registration parameters. Parameters that have not changed should not be
	 * sent to reduce the size of the message.
	 * 
	 * @param lifetime
	 *            lifetime of this endpoint
	 * @param context
	 *            scheme, host and port at which the endpoint is available
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
		// Reset endpoint lifetime.
		updateScheduledFuture();
		if (context != null) {
			this.context = context;
			getAttributes().clearAttribute(LinkFormat.CONTEXT);
			getAttributes().addAttribute(LinkFormat.CONTEXT, this.context);
		}
	}

	/**
	 * Updates child resources of this endpoint. This method can be used by
	 * {@link RdResource} upon receiving a registration update request to update
	 * this endpoint's child resources. The old resources will be deleted.
	 * 
	 * The parameter of this method takes a list of resources which are stored
	 * in the CoRE Link Format. Method {@link LinkFormat #parse(String)} is used
	 * to parse this list of resource into a {@link Set} of {@link WebLink}
	 * elements.
	 * 
	 * @param linkFormat
	 *            list of resources in CoRE Link Format
	 */
	public void updateResources(String linkFormat) {
		Set<WebLink> links = LinkFormat.parse(linkFormat);
		String uri;
		CoapResource resource = this;
		CoapResource childResource = null;

		// Clear old children.
		getChildren().clear();

		for (WebLink link : links) {
			uri = link.getURI();
			try (Scanner uriScanner = new Scanner(uri).useDelimiter("/")) {
				while (uriScanner.hasNext()) {
					childResource = new CoapResource(uriScanner.next());
					resource.add(childResource);
					resource = childResource;
				}

			}
			for (String attr : link.getAttributes().getAttributeKeySet()) {
				for (String value : link.getAttributes().getAttributeValues(attr)) {
					resource.getAttributes().addAttribute(attr, value);
				}
			}
			resource = this;
		}
	}

	private void updateScheduledFuture() {
		if (lifetime != null) {
			if (scheduledFuture != null) {
				scheduledFuture.cancel(true);
			}
			scheduledFuture = SCHEDULER.schedule(new Runnable() {

				@Override
				public void run() {
					ENDPOINT_LOGGER.log(Level.INFO, "Endpoint lifetime has expired: {0}",
							new Object[] { EndpointResource.this.toString() });
					delete();
				}
				// Add two seconds to prevent endpoint being removed before the
				// next update due to package loss or slow network.
			}, lifetime + 2, TimeUnit.SECONDS);
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
