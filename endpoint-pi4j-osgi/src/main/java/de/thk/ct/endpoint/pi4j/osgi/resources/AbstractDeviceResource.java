package de.thk.ct.endpoint.pi4j.osgi.resources;

import de.thk.ct.base.ActuatorType;
import de.thk.ct.base.SensorType;

/**
 * Superclass for all sensors and actuators in this application. It defines
 * common attributes and methods for these resources. It notifies
 * {@link DeviceResourceListener} if child class fires the
 * {@link AbstractDeviceResource #onChanged(String)} method and if the listener
 * is initialized.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class AbstractDeviceResource {

	protected String name;

	private DeviceResourceListener listener;

	/**
	 * Constructs the {@link AbstractDeviceResource} with the given name and
	 * listener.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 */
	public AbstractDeviceResource(String name, DeviceResourceListener listener) {
		this.name = name;
		this.listener = listener;
	}

	/**
	 * Initializes resource (e.g. provisions GPIO pins).
	 */
	public abstract void init();

	/**
	 * Destroys resource (e.g. unprovisions GPIO pins).
	 */
	public abstract void destroy();

	/**
	 * Returns either a {@link SensorType} or a {@link ActuatorType} of this
	 * resource as a string value.
	 * 
	 * @return resource type
	 */
	public abstract String getType();

	/**
	 * Reads the current state of the resource (e.g. state of temperature
	 * sensor).
	 * 
	 * @return
	 */
	public abstract String getValue();

	/**
	 * Notifies the {@link DeviceResourceListener} about change of the resource
	 * state (e.g. button has been pressed).
	 * 
	 * @param value
	 */
	protected void onChanged(String value) {
		if (listener != null) {
			listener.onChanged(value);
		}
	}

	public String getName() {
		return name;
	}
}
