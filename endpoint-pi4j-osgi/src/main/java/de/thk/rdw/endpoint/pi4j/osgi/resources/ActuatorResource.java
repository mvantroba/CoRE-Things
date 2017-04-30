package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.ActuatorType;

public abstract class ActuatorResource extends AbstractDeviceResource {

	protected ActuatorType actuatorType;

	public ActuatorResource(String name, DeviceResourceListener listener, ActuatorType actuatorType) {
		super(name, listener);
		this.actuatorType = actuatorType;
	}

	public abstract void toggle();

	public abstract void setValue(String value);

	@Override
	public String getType() {
		return actuatorType.getType();
	}

	public ActuatorType getActuatorType() {
		return actuatorType;
	}
}
