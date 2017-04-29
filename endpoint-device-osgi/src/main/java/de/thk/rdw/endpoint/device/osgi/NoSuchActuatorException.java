package de.thk.rdw.endpoint.device.osgi;

public class NoSuchActuatorException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchActuatorException(String message) {
		super(message);
	}
}
