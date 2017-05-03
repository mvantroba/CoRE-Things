package de.thk.ct.endpoint.pi4j.osgi.resources;

/**
 * Listener that is used to listen for changes on a
 * {@link AbstractDeviceResource}. Every sensor and actuator in this application
 * should use this listener.
 * 
 * @author Martin Vantroba
 *
 */
public interface DeviceResourceListener {

	/**
	 * Callback method that is called when a sensor's or an actuator's state has
	 * changed.
	 * 
	 * @param value
	 *            new value
	 */
	void onChanged(String value);
}
