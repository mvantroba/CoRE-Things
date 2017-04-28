package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.SensorType;

public abstract class SensorPi4jResource extends AbstractPi4jResource {

	private SensorType sensorType;

	public SensorPi4jResource(String name, SensorType sensorType) {
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
