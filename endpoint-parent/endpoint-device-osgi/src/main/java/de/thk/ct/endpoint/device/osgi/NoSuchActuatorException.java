package de.thk.ct.endpoint.device.osgi;

/**
 * {@link Exception} that implies that the actuator with the given ID does not
 * exist on a device.
 * 
 * @author Martin Vantroba
 *
 */
public class NoSuchActuatorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@link NoSuchActuatorException} with a predefined message
	 * which includes the given actuator ID.
	 * 
	 * @param id
	 *            actuator id
	 */
	public NoSuchActuatorException(int id) {
		super(String.format("Actuator with ID %d does not exist on the device.", id));
	}
}
