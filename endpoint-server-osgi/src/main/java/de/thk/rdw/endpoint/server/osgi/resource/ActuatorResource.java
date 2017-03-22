package de.thk.rdw.endpoint.server.osgi.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.rdw.endpoint.server.osgi.DeviceServiceNotInitializedException;

public abstract class ActuatorResource extends CoapResource {

	public ActuatorResource(String name) {
		super(name);
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
