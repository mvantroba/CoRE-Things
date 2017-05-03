package de.thk.ct.endpoint.device.osgi;

public interface DeviceListener {

	void onSensorChanged(int id, String value);

	void onActuatorChanged(int id, String value);
}
