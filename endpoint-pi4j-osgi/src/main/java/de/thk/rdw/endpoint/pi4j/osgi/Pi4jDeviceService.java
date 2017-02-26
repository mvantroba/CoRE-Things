package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	@Override
	public void toggleActuator(String name) {
		LOGGER.log(Level.INFO, "Toggling actuator with name \"{0}\".", new Object[] { name });
	}
}
