package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.SensorType;

public abstract class SensorResource extends AbstractDeviceResource {

	protected SensorType sensorType;

	public SensorResource(String name, DeviceResourceListener listener, SensorType sensorType) {
		super(name, listener);
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
