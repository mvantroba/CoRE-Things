package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class EndpointPi4jActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointPi4jActivator.class.getName());

	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Endpoint Pi4J\"...");
		DeviceService deviceService = new Pi4jDeviceService();
		LOGGER.log(Level.INFO, "Registering service \"{0}\"...", new Object[] { DeviceService.class.getName() });
		context.registerService(DeviceService.class, deviceService, null);
		LOGGER.log(Level.INFO, "Service \"{0}\" is registered.", new Object[] { DeviceService.class.getName() });
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Pi4J\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Endpoint Pi4J\"...");
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Pi4J\" is stopped.");
	}
}