package de.thk.ct.endpoint.server.osgi.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.ct.base.SensorType;

/**
 * REST interface which models a sensor resource on a device.
 * 
 * @author Martin Vantroba
 *
 */
public class SensorCoapResource extends CoapResource {

	private String value;

	/**
	 * Constructs a {@link SensorCoapResource} with the given name and type and
	 * enables observation of this resource.
	 * 
	 * @param name
	 *            sensor name
	 * @param sensorType
	 *            sensor type
	 */
	public SensorCoapResource(String name, SensorType sensorType) {
		super(name);
		getAttributes().addResourceType(sensorType.getType());
		setObservable(true);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, value);
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
