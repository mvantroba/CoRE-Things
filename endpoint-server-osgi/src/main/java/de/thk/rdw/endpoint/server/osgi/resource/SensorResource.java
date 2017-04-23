package de.thk.rdw.endpoint.server.osgi.resource;

import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;

public abstract class SensorResource extends CoapResource {

	private String sensorValue;

	public SensorResource(String name) {
		super(name);
		setObservable(true);
	}

	public String getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(String sensorValue) {
		this.sensorValue = sensorValue;
		changed();
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(sensorValue);
		if (exchange.getRequestOptions().hasObserve()) {
			ObservingEndpoint observingEndpoint = new ObservingEndpoint(
					new InetSocketAddress(exchange.getSourceAddress(), exchange.getSourcePort()));
			addObserveRelation(new ObserveRelation(observingEndpoint, this, exchange.advanced()));
			LOGGER.log(Level.INFO, "Added endpoint to observers: {0}", observingEndpoint.getAddress().toString());
		}
	}

	protected abstract String onGetValue();
}
