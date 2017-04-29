package de.thk.rdw.endpoint.device.osgi;

public class NoSuchSensorException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchSensorException(String message) {
		super(message);
	}
}
