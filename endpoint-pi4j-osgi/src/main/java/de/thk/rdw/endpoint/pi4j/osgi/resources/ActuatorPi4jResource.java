package de.thk.rdw.endpoint.pi4j.osgi.resources;

import de.thk.rdw.base.ActuatorType;

public abstract class ActuatorPi4jResource extends AbstractPi4jResource {

	private ActuatorType actuatorType;

	public ActuatorPi4jResource(String name, ActuatorType actuatorType) {
		super(name);
		this.actuatorType = actuatorType;
	}

	@Override
	public String getType() {
		return actuatorType.getType();
	}

	public ActuatorType getActuatorType() {
		return actuatorType;
	}
}
