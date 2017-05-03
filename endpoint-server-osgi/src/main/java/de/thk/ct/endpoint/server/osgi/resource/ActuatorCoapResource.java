package de.thk.ct.endpoint.server.osgi.resource;

import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.ct.base.ActuatorType;
import de.thk.ct.endpoint.server.osgi.DeviceServiceNotInitializedException;

public abstract class ActuatorCoapResource extends CoapResource {

	private String value;

	public ActuatorCoapResource(String name, ActuatorType actuatorType) {
		super(name);
		getAttributes().addResourceType(actuatorType.getType());
		setObservable(true);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		changed();
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(value);
		if (exchange.getRequestOptions().hasObserve()) {
			ObservingEndpoint observingEndpoint = new ObservingEndpoint(
					new InetSocketAddress(exchange.getSourceAddress(), exchange.getSourcePort()));
			addObserveRelation(new ObserveRelation(observingEndpoint, this, exchange.advanced()));
			LOGGER.log(Level.INFO, "Added endpoint to observers: {0}", observingEndpoint.getAddress().toString());
		}
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		Response response;
		try {
			onToggle();
			response = new Response(ResponseCode.CHANGED);
		} catch (DeviceServiceNotInitializedException e) {
			response = new Response(ResponseCode.SERVICE_UNAVAILABLE);
			response.setPayload(e.getMessage());
		}
		exchange.respond(response);
	}

	protected abstract void onToggle() throws DeviceServiceNotInitializedException;
}
