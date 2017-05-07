package de.thk.ct.endpoint.server.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.LinkFormat;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.thk.ct.endpoint.device.osgi.DeviceListener;
import de.thk.ct.endpoint.device.osgi.DeviceService;
import de.thk.ct.endpoint.server.osgi.network.EndpointClient;
import de.thk.ct.endpoint.server.osgi.network.EndpointServer;

/**
 * The {@link BundleActivator} of this module. It starts an
 * {@link EndpointServer} and {@link EndpointClient} in the
 * {@link #start(BundleContext)} method. It also opens a {@link ServiceTracker}
 * for {@link DeviceService} and updates the server and client resources when
 * there is a change in the service registry.
 * <p>
 * In the {@link #stop(BundleContext)} method of the bundle, all resources are
 * destroyed.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointServerActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointServerActivator.class.getName());

	private ServiceTracker<DeviceService, DeviceService> deviceServiceTracker;

	private EndpointServer endpointServer;
	private EndpointClient endpointClient;

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"CoRE Things Endpoint Server\"...");

		// Start endpoint server.
		endpointServer = new EndpointServer();
		endpointServer.start();

		// Start endpoint client.
		endpointClient = new EndpointClient();
		endpointClient.setRegistrationPayload(LinkFormat.serializeTree(endpointServer.getRoot()));
		endpointClient.startRegistration();

		// Open service tracker.
		DeviceServiceTrackerCustomizer deviceServiceTrackerCustomizer = new DeviceServiceTrackerCustomizer(context);
		deviceServiceTracker = new ServiceTracker<>(context, DeviceService.class, deviceServiceTrackerCustomizer);
		deviceServiceTracker.open();

		LOGGER.log(Level.INFO, "Bundle \"CoRE Things Endpoint Server\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"CoRE Things Endpoint Server\"...");

		// Close service tracker.
		deviceServiceTracker.close();

		// Shutdown endpoint client.
		endpointClient.resetRegistration();
		endpointClient.shutdown();

		// Destroy endpoint server.
		endpointServer.destroy();

		LOGGER.log(Level.INFO, "Bundle \"CoRE Things Endpoint Server\" is stopped.");
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

			// Update REST resources in the server.
			endpointServer.setDeviceService(service);

			// Initialize device listener which will notify endpoint server
			// about changes on the device.
			deviceListener = new DeviceListener() {

				@Override
				public void onSensorChanged(int id, String value) {
					endpointServer.sensorChanged(id, value);
				}

				@Override
				public void onActuatorChanged(int id, String value) {
					endpointServer.actuatorChanged(id, value);
				}
			};
			service.addListener(deviceListener);

			// Obtain serialized list of new resources from the server and give
			// it to client, so it can register them in the resource directory.
			endpointClient.setRegistrationPayload(LinkFormat.serializeTree(endpointServer.getRoot()));
			endpointClient.startRegistration();

			return service;
		}

		@Override
		public void modifiedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Modifying service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			// Update REST resources in the server.
			endpointServer.setDeviceService(service);

			// Obtain serialized list of new resources from the server and give
			// it to client, so it can register them in the resource directory.
			endpointClient.setRegistrationPayload(LinkFormat.serializeTree(endpointServer.getRoot()));
			endpointClient.startRegistration();
		}

		@Override
		public void removedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Removing service \"{0}\"...", new Object[] { DeviceService.class.getName() });

			// Remove all REST sensors and actuator from the server and remove
			// device listener from device service.
			endpointServer.unsetDeviceService();
			if (deviceListener != null) {
				service.removeListener(deviceListener);
			}

			// Serialize the remaining server resources and update the client.
			endpointClient.setRegistrationPayload(LinkFormat.serializeTree(endpointServer.getRoot()));
			endpointClient.startRegistration();

			context.ungetService(reference);
		}
	}
}
