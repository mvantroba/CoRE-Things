package de.thk.ct.endpoint.device.osgi;

/**
 * {@link Exception} that implies that the sensor with the given ID does not
 * exist on a device.
 * 
 * @author Martin Vantroba
 *
 */
public class NoSuchSensorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@link NoSuchSensorException} with a predefined message
	 * which includes the given sensor ID.
	 * 
	 * @param id
	 *            sensor id
	 */
	public NoSuchSensorException(int id) {
		super(String.format("Sensor with ID %d does not exist on the device.", id));
	}
}
