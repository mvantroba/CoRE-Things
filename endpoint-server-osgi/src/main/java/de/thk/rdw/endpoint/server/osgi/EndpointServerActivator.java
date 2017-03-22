package de.thk.rdw.endpoint.server.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class EndpointServerActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointServerActivator.class.getName());

	private EndpointServer endpointServer;
	private ServiceTracker<DeviceService, DeviceService> deviceServiceTracker;

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Endpoint Server\"...");
		endpointServer = new EndpointServer();
		endpointServer.start();

		CoapClient coapClient = new CoapClient(
				"coap://192.168.0.100/rd?ep=node1&et=raspberrypi&con=coap://192.168.0.101:5683");
		LOGGER.log(Level.INFO, "Sending registration request. URI: {0}", new Object[] { coapClient.getURI() });
		CoapResponse response = coapClient.post("</a/1/led>rt=\"led\"", MediaTypeRegistry.TEXT_PLAIN);
		LOGGER.log(Level.INFO, "Response from server: {0}", new Object[] { response.toString() });

		DeviceServiceTrackerCustomizer deviceServiceTrackerCustomizer = new DeviceServiceTrackerCustomizer(context);
		deviceServiceTracker = new ServiceTracker<>(context, DeviceService.class, deviceServiceTrackerCustomizer);
		deviceServiceTracker.open();
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Endpoint Server\"...");
		deviceServiceTracker.close();
		endpointServer.stop();
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is stopped.");
	}

	private class DeviceServiceTrackerCustomizer implements ServiceTrackerCustomizer<DeviceService, DeviceService> {

		private final BundleContext context;

		public DeviceServiceTrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public DeviceService addingService(ServiceReference<DeviceService> reference) {
			LOGGER.log(Level.INFO, "Adding service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			DeviceService service = context.getService(reference);
			endpointServer.setDeviceService(service);
			return service;
		}

		@Override
		public void modifiedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Modifying service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			// TODO Auto-generated method stub
		}

		@Override
		public void removedService(ServiceReference<DeviceService> reference, DeviceService service) {
			LOGGER.log(Level.INFO, "Removing service \"{0}\"...", new Object[] { DeviceService.class.getName() });
			endpointServer.unsetDeviceService();
			context.ungetService(reference);
		}
	}
}
