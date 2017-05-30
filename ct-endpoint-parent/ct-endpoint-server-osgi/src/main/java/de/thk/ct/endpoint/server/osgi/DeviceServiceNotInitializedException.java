package de.thk.ct.endpoint.server.osgi;

import de.thk.ct.endpoint.device.osgi.DeviceService;

/**
 * {@link Exception} which indicates that there is no available implementation
 * of {@link DeviceService}.
 * 
 * @author Martin Vantroba
 *
 */
public class DeviceServiceNotInitializedException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@link DeviceServiceNotInitializedException} with the
	 * predefined message.
	 */
	public DeviceServiceNotInitializedException() {
		super("Device service is not initialized.");
	}
}
