package de.thk.rdw.endpoint.device.osgi;

import java.util.Map;
import java.util.Map.Entry;

public interface DeviceService {

	Map<Integer, Entry<String, String>> getSensors();

	Map<Integer, Entry<String, String>> getActuators();

	String getSensorValue(int id) throws NoSuchSensorException;

	String getActuatorValue(int id) throws NoSuchActuatorException;

	void setActuatorValue(int id, String value) throws NoSuchActuatorException;

	void toggleActuator(int id) throws NoSuchActuatorException;

	boolean addListener(DeviceListener deviceListener);

	boolean removeListener(DeviceListener deviceListener);
}
