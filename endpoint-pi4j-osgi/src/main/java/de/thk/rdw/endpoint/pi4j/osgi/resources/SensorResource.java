package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.SensorType;

public abstract class SensorResource extends AbstractPi4jResource {

	protected SensorType sensorType;

	public SensorResource(String name, SensorType sensorType) {
		super(name);
		this.sensorType = sensorType;
	}

	@Override
	public String getType() {
		return sensorType.getType();
	}

	public SensorType getSensorType() {
		return sensorType;
	}
}
