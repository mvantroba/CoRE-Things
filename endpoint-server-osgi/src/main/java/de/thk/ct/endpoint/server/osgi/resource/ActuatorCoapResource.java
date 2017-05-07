package de.thk.ct.endpoint.server.osgi.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.ct.base.ActuatorType;
import de.thk.ct.endpoint.server.osgi.DeviceServiceNotInitializedException;

/**
 * REST interface which models an actuator resource on a device.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class ActuatorCoapResource extends CoapResource {

	private String value;

	/**
	 * Constructs an {@link ActuatorCoapResource} with the given name and type
	 * and enables observation of this resource.
	 * 
	 * @param name
	 *            actuator name
	 * @param actuatorType
	 *            actuator type
	 */
	public ActuatorCoapResource(String name, ActuatorType actuatorType) {
		super(name);
		getAttributes().addResourceType(actuatorType.getType());
		setObservable(true);
	}

	/**
	 * Callback method which is called when client sends a PUT request with new
	 * actuator value.
	 * 
	 * @param value
	 *            new actuator value
	 * @return resulting CoAP response code
	 * @throws DeviceServiceNotInitializedException
	 *             device service is not initialized
	 */
	protected abstract ResponseCode onSetValue(String value) throws DeviceServiceNotInitializedException;

	/**
	 * Callback method which is called when client sends a POST request to
	 * toggle an actuator.
	 * 
	 * @return resulting response code
	 * @throws DeviceServiceNotInitializedException
	 *             device service is not initialized
	 */
	protected abstract ResponseCode onToggle() throws DeviceServiceNotInitializedException;

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(value);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		ResponseCode responseCode;
		try {
			responseCode = onToggle();
			changed();
		} catch (DeviceServiceNotInitializedException e) {
			responseCode = ResponseCode.SERVICE_UNAVAILABLE;
		}
		exchange.respond(responseCode);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		ResponseCode responseCode;
		try {
			responseCode = onSetValue(exchange.getRequestText());
			changed();
		} catch (DeviceServiceNotInitializedException e) {
			responseCode = ResponseCode.SERVICE_UNAVAILABLE;
		}
		exchange.respond(responseCode);
	}

	/**
	 * Updates the value and notifies all observers about state change.
	 * 
	 * @param value
	 *            new value
	 */
	public void setValue(String value) {
		this.value = value;
		changed();
	}
}
