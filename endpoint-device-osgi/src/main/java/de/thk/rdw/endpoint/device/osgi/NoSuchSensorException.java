package de.thk.rdw.endpoint.device.osgi;

public class NoSuchSensorException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchSensorException(int id) {
		super(String.format("Sensor with ID %d does not exist on the device.", id));
	}
}
