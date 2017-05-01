package de.thk.rdw.endpoint.device.osgi;

public class NoSuchActuatorException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchActuatorException(int id) {
		super(String.format("Actuator with ID %d does not exist on the device.", id));
	}
}
