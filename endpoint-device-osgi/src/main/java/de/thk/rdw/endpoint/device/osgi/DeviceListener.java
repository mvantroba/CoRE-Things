package de.thk.rdw.endpoint.device.osgi;

public interface DeviceListener {

	void onSensorChanged(Integer íd, Object newValue);
}
