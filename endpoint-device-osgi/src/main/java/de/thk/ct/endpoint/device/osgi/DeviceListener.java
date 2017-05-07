package de.thk.ct.endpoint.device.osgi;

/**
 * Listener interface which enables components to register themselves in a
 * {@link DeviceService} and be notified about state changes of sensors and
 * actuators.
 * 
 * @author Martin Vantroba
 *
 */
public interface DeviceListener {

	/**
	 * Callback method which will be called after certain sensor changed its
	 * state (e.g. temperature value has raised).
	 * 
	 * @param id
	 *            sensor id
	 * @param value
	 *            new value
	 */
	void onSensorChanged(int id, String value);

	/**
	 * Callback method which will be called after certain actuator changed its
	 * state (e.g. LED has been toggled).
	 * 
	 * @param id
	 *            actuator id
	 * @param value
	 *            new value
	 */
	void onActuatorChanged(int id, String value);
}
