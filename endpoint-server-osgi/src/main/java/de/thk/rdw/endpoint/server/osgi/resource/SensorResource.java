package de.thk.rdw.endpoint.server.osgi.resource;

import org.eclipse.californium.core.CoapResource;

public abstract class SensorResource extends CoapResource {

	public SensorResource(String name) {
		super(name);
	}

	protected abstract String onGetValue();
}
