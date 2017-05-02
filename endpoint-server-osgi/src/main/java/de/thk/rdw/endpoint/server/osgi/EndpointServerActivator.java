package de.thk.rdw.endpoint.server.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.LinkFormat;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.server.osgi.network.EndpointClient;
import de.thk.rdw.endpoint.server.osgi.network.EndpointServer;

public class EndpointServerActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointServerActivator.class.getName());

	private ServiceTracker<DeviceService, DeviceService> deviceServiceTracker;

	private EndpointServer endpointServer;
	private EndpointClient endpointClient;

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Endpoint Server\"...");

		endpointServer = new EndpointServer();
		endpointClient = new EndpointClient();
		endpointClient.sendRegistrationRequest(LinkFormat.serializeTree(endpointServer.getRoot()));
		endpointServer.start();

		DeviceServiceTrackerCustomizer deviceServiceTrackerCustomizer = new DeviceServiceTrackerCustomizer(context);
		deviceServiceTracker = new ServiceTracker<>(context, DeviceService.class, deviceServiceTrackerCustomizer);
		deviceServiceTracker.open();

		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Endpoint Server\"...");
		deviceServiceTracker.close();

		endpointClient.sendRemovalRequest();
		endpointClient.shutdown();
		endpointServer.stop();

		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is stopped.");
	}

	private class DeviceServiceTrackerCustomizer implements ServiceTrackerCustomizer<DeviceService, DeviceService> {

		private final BundleContext context;
		private DeviceListener deviceListener;

		public DeviceServiceTrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public DeviceService addingService(ServiceReference<DeviceService> reference) {
			LOGGER.log(Level.INFO, "Adding service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			DeviceService service = context.getService(reference);
			endpointServer.setDeviceService(service);
			deviceListener = new DeviceListener() {

				@Override
				public void onSensorChanged(int id, String value) {
					LOGGER.log(Level.INFO, "Sensor with ID {0} changed state to {1}.", new Object[] { id, value });
					endpointServer.sensorChanged(id, value);
				}

				@Override
				public void onActuatorChanged(int id, String value) {
					LOGGER.log(Level.INFO, "Actuator with ID {0} changed state to {1}.", new Object[] { id, value });
					endpointServer.actuatorChanged(id, value);
				}
			};
			service.addListener(deviceListener);
			endpointClient.sendRegistrationRequest(LinkFormat.serializeTree(endpointServer.getRoot()));
			return service;
		}

		@Override
		public void modifiedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Modifying service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			endpointClient.sendRegistrationRequest(LinkFormat.serializeTree(endpointServer.getRoot()));
		}

		@Override
		public void removedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Removing service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			endpointServer.unsetDeviceService();
			if (deviceListener != null) {
				service.removeListener(deviceListener);
			}
			context.ungetService(reference);
			endpointClient.sendRegistrationRequest(LinkFormat.serializeTree(endpointServer.getRoot()));
		}
	}
}
