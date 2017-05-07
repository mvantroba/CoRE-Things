package de.thk.ct.endpoint.server.osgi.resource;

import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
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

		if (exchange.getRequestOptions().hasObserve()) {
			ObservingEndpoint observingEndpoint = new ObservingEndpoint(
					new InetSocketAddress(exchange.getSourceAddress(), exchange.getSourcePort()));

			addObserveRelation(new ObserveRelation(observingEndpoint, this, exchange.advanced()));
			LOGGER.log(Level.INFO, "Added endpoint to observers: {0}", observingEndpoint.getAddress().toString());
		}
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
