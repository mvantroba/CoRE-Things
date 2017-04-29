package de.thk.rdw.endpoint.device.osgi;

public interface DeviceListener {

	void onSensorChanged(int id, String value);

	void onActuatorChanged(int id, String value);
}
