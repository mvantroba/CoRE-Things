package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.ActuatorType;

public abstract class ActuatorResource extends AbstractPi4jResource {

	protected ActuatorType actuatorType;

	public ActuatorResource(String name, ActuatorType actuatorType) {
		super(name);
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
