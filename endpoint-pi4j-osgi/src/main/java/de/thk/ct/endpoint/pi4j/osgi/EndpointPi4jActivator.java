package de.thk.ct.endpoint.pi4j.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.thk.ct.endpoint.device.osgi.DeviceService;

/**
 * The {@link BundleActivator} of this bundle. It initializes and starts
 * {@link Pi4jDeviceService} which can be used to access sensors and actuators
 * on the device.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointPi4jActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointPi4jActivator.class.getName());

	private Pi4jDeviceService deviceService;

	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"CoRE Things Endpoint Pi4J\"...");
		deviceService = new Pi4jDeviceService();
		deviceService.init();
		LOGGER.log(Level.INFO, "Registering service \"{0}\"...", new Object[] { DeviceService.class.getName() });
		context.registerService(DeviceService.class, deviceService, null);
		LOGGER.log(Level.INFO, "Service \"{0}\" is registered.", new Object[] { DeviceService.class.getName() });
		LOGGER.log(Level.INFO, "Bundle \"CoRE Things Endpoint Pi4J\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"CoRE Things Endpoint Pi4J\"...");
		deviceService.destroy();
		LOGGER.log(Level.INFO, "Bundle \"CoRE Things Endpoint Pi4J\" is stopped.");
	}
}
