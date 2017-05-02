package de.thk.rdw.endpoint.server.osgi.resource;

import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.server.osgi.DeviceServiceNotInitializedException;

public abstract class SensorCoapResource extends CoapResource {

	private String value;

	public SensorCoapResource(String name, SensorType sensorType) {
		super(name);
		getAttributes().addResourceType(sensorType.getType());
		setObservable(true);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String sensorValue) {
		this.value = sensorValue;
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

	protected abstract String onGetValue() throws DeviceServiceNotInitializedException;
}
