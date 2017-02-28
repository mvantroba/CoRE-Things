package de.thk.rdw.rd.resources;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

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
		}
		this.endpointType = variables.get(UriVariable.END_POINT_TYPE);
		updateVariables(variables);
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
		updateLifetime(lifetime);
		this.context = variables.get(UriVariable.CONTEXT);
	}

	@Override
	public String toString() {
		return " [name=" + getName() + ", domain=" + domain + ", endpointType=" + endpointType + ", lifetime="
				+ lifetime + ", context=" + context + "]";
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

	public void updateLifetime(Long lifetime) {
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
