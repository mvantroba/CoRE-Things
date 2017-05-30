package de.thk.ct.endpoint.pi4j.osgi.resources;

import de.thk.ct.endpoint.device.osgi.resources.ActuatorType;

/**
 * Superclass for all actuators in this application. Every actuator has to be
 * type of the {@link ActuatorType}.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class ActuatorResource extends AbstractDeviceResource {

	protected ActuatorType actuatorType;

	/**
	 * Constructs the {@link ActuatorResource} with the given name, listener and
	 * type.
	 * 
	 * @param name
	 *            actuator name
	 * @param listener
	 *            actuator listener
	 * @param actuatorType
	 *            actuator type
	 */
	public ActuatorResource(String name, DeviceResourceListener listener, ActuatorType actuatorType) {
		super(name, listener);
		this.actuatorType = actuatorType;
	}

	/**
	 * Toggles the actuator (e.g. turns on a LED).
	 */
	public abstract void toggle();

	/**
	 * Sets the actuator to the given value (e.g. increases speed of a servo).
	 * 
	 * @param value
	 *            new value
	 * @throws IllegalArgumentException
	 *             value is invalid
	 */
	public abstract void setValue(String value) throws IllegalArgumentException;

	@Override
	public String getType() {
		return actuatorType.getType();
	}

	public ActuatorType getActuatorType() {
		return actuatorType;
	}
}
