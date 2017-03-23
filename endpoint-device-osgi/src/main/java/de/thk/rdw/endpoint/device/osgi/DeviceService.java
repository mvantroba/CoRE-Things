package de.thk.rdw.endpoint.device.osgi;

public interface DeviceService {

	boolean addListener(DeviceListener deviceListener);

	boolean removeListener(DeviceListener deviceListener);

	void toggleActuator(String name);
}
