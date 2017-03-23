package de.thk.rdw.endpoint.device.osgi;

public interface DeviceListener {

	void onSensorChanged(String name, Object newValue);
}
