package de.thk.ct.endpoint.device.osgi;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Service which defines methods that can be used to retrieve all sensors and
 * actuators on a device, get sensor and actuator value, control actuator and
 * register and remove a {@link DeviceListener}.
 * 
 * @author Martin Vantroba
 *
 */
public interface DeviceService {

	/**
	 * Retrieves all sensors on a device and stores them as a {@link Map} with
	 * following values: Map&lt;ID, Entry&lt;Type, Name&gt;&gt;.
	 * 
	 * @return all sensors on a device
	 */
	Map<Integer, Entry<String, String>> getSensors();

	/**
	 * Retrieves all actuators on a device and stores them as a {@link Map} with
	 * following values: Map&lt;ID, Entry&lt;Type, Name&gt;&gt;.
	 * 
	 * @return all actuators on a device
	 */
	Map<Integer, Entry<String, String>> getActuators();

	/**
	 * Retrieves the current value of a sensor (e.g. temperature).
	 * 
	 * @param id
	 *            sensor ID
	 * @return current value
	 * @throws NoSuchSensorException
	 *             sensor with the given ID does not exist on the device
	 */
	String getSensorValue(int id) throws NoSuchSensorException;

	/**
	 * Retrieves the current value of an actuator (e.g. state of an LED).
	 * 
	 * @param id
	 *            actuator ID
	 * @return current value
	 * @throws NoSuchActuatorException
	 *             actuator with the given ID does not exist on the device
	 */
	String getActuatorValue(int id) throws NoSuchActuatorException;

	/**
	 * Sets a state of an actuator (e.g. sets LED state to "1");
	 * 
	 * @param id
	 *            actuator ID
	 * @param value
	 *            new state
	 * @throws NoSuchActuatorException
	 *             actuator with the given ID does not exist on the device
	 */
	void setActuatorValue(int id, String value) throws NoSuchActuatorException;

	/**
	 * Toggles an actuator with the given ID.
	 * 
	 * @param id
	 *            actuator ID
	 * @throws NoSuchActuatorException
	 *             actuator with the given ID does not exist on the device
	 */
	void toggleActuator(int id) throws NoSuchActuatorException;

	/**
	 * Adds a {@link DeviceListener} which will be notified about state changes
	 * of sensors and actuators.
	 * 
	 * @param deviceListener
	 *            device listener
	 * @return true if successful
	 */
	boolean addListener(DeviceListener deviceListener);

	/**
	 * Removes {@link DeviceListener} from the list so it wont be notified about
	 * state changes of sensors and actuators anymore.
	 * 
	 * @param deviceListener
	 *            device listener
	 * @return true if successful
	 */
	boolean removeListener(DeviceListener deviceListener);
}
