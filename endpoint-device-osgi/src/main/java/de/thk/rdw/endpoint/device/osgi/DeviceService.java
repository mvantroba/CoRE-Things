package de.thk.rdw.endpoint.device.osgi;

import java.util.Map;
import java.util.Map.Entry;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.SensorType;

public interface DeviceService {

	Map<Integer, Entry<SensorType, String>> getSensors();

	Map<Integer, Entry<ActuatorType, String>> getActuators();

	Object getSensorValue(int id);

	Object getActuatorValue(int id);

	void setActuatorValue(int id, Object value);

	void toggleActuator(int id);

	boolean addListener(DeviceListener deviceListener);

	boolean removeListener(DeviceListener deviceListener);
}
