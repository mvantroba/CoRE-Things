package de.thk.rdw.endpoint.server.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class EndpointServerActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointServerActivator.class.getName());

	// TODO Configure these properties in a file.
	private static final String EP_NAME = "node1";
	private static final String EP_TYPE = "raspberrypi";
	private static final String RD_SCHEME = CoAP.COAP_URI_SCHEME;
	private static final String RD_HOST = "192.168.0.100";
	private static final int RD_PORT = CoAP.DEFAULT_COAP_PORT;
	private static final String RD_PATH = "rd";

	private EndpointServer endpointServer;
	private ServiceTracker<DeviceService, DeviceService> deviceServiceTracker;

	private CoapClient coapClient;

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Endpoint Server\"...");
		endpointServer = new EndpointServer();
		endpointServer.start();

		// TODO Read endpoint URI from endpoint server.
		String query = String.format("ep=%s&et=%s&con=%s", EP_NAME, EP_TYPE, "coap://192.168.0.101:5683");
		coapClient = new CoapClient.Builder(RD_HOST, RD_PORT).scheme(RD_SCHEME).path(RD_PATH).query(query).create();
		LOGGER.log(Level.INFO, "Sending registration request to \"{0}\".", new Object[] { coapClient.getURI() });
		coapClient.post(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				LOGGER.log(Level.INFO, "Received response from server. Code: \"{0}\", Payload: \"{1}\"",
						new Object[] { response.getCode(), response.getPayload() });
			}

			@Override
			public void onError() {
				LOGGER.log(Level.INFO, "Error has occured while sending registration request to \"{0}\".",
						new Object[] { coapClient.getURI() });
			}
		}, LinkFormat.serializeTree(endpointServer.getRoot()), MediaTypeRegistry.TEXT_PLAIN);

		DeviceServiceTrackerCustomizer deviceServiceTrackerCustomizer = new DeviceServiceTrackerCustomizer(context);
		deviceServiceTracker = new ServiceTracker<>(context, DeviceService.class, deviceServiceTrackerCustomizer);
		deviceServiceTracker.open();
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Endpoint Server\"...");
		deviceServiceTracker.close();
		coapClient.shutdown();
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
				public void onSensorChanged(String name, Object newValue) {
					LOGGER.log(Level.INFO, "Sensor \"{0}\" has changed. New value: {1}",
							new Object[] { name, newValue });
				}
			};
			service.addListener(deviceListener);
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
			if (deviceListener != null) {
				service.removeListener(deviceListener);
			}
			context.ungetService(reference);
		}
	}
}
