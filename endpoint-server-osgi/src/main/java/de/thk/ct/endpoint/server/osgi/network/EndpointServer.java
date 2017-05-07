package de.thk.ct.endpoint.server.osgi.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

import de.thk.ct.base.ActuatorType;
import de.thk.ct.base.ResourceType;
import de.thk.ct.base.SensorType;
import de.thk.ct.endpoint.device.osgi.DeviceService;
import de.thk.ct.endpoint.device.osgi.NoSuchActuatorException;
import de.thk.ct.endpoint.server.osgi.DeviceServiceNotInitializedException;
import de.thk.ct.endpoint.server.osgi.resource.ActuatorCoapResource;
import de.thk.ct.endpoint.server.osgi.resource.SensorCoapResource;

/**
 * The {@link CoapServer} which provides a REST interface for management of
 * sensors and actuators that are obtained from the current
 * {@link DeviceService}.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointServer extends CoapServer {

	private static final Logger LOGGER = Logger.getLogger(EndpointServer.class.getName());

	private DeviceService deviceService;

	private CoapResource sensorsResource; // main sensor resource
	private CoapResource actuatorsResource; // main actuator resource

	private NavigableMap<Integer, ActuatorCoapResource> actuators = new TreeMap<>();
	private NavigableMap<Integer, SensorCoapResource> sensors = new TreeMap<>();

	public EndpointServer() {
		super();
		// Bind endpoints to each network interface.
		for (InetAddress address : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (!address.isLinkLocalAddress()) {
				this.addEndpoint(new CoapEndpoint(new InetSocketAddress(address, CoAP.DEFAULT_COAP_PORT)));
			}
		}

		// Init main sensor resource.
		sensorsResource = new CoapResource(ResourceType.SENSOR.getName());
		sensorsResource.getAttributes().addResourceType(ResourceType.SENSOR.getType());

		// Init main actuator resource.
		actuatorsResource = new CoapResource(ResourceType.ACTUATOR.getName());
		actuatorsResource.getAttributes().addResourceType(ResourceType.ACTUATOR.getType());

		add(sensorsResource, actuatorsResource);
	}

	/**
	 * Initializes {@link DeviceService}, obtains all its sensors and actuators
	 * and creates corresponding REST resources.
	 * 
	 * @param deviceService
	 *            device service
	 */
	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;

		// Obtain all sensors from device service.
		for (Entry<Integer, Entry<String, String>> entry : deviceService.getSensors().entrySet()) {
			Entry<String, String> sensor = entry.getValue();
			SensorType sensorType = SensorType.get(sensor.getKey());
			if (sensorType != null) {
				addSensor(entry.getKey(), sensorType, sensor.getValue());
			} else {
				LOGGER.log(Level.WARNING, "Sensor type \"{0}\" is unknown.", sensor.getKey());
			}
		}

		// Obtain all actuators from device service.
		for (Entry<Integer, Entry<String, String>> entry : deviceService.getActuators().entrySet()) {
			Entry<String, String> actuator = entry.getValue();
			ActuatorType actuatorType = ActuatorType.get(actuator.getKey());
			if (actuatorType != null) {
				addActuator(entry.getKey(), actuatorType, actuator.getValue());
			} else {
				LOGGER.log(Level.WARNING, "Actuator type \"{0}\" is unknown.", actuator.getKey());
			}
		}
	}

	/**
	 * Removes all sensors and actuator resources from the REST interface.
	 */
	public void unsetDeviceService() {
		this.deviceService = null;

		// Clear all sensors and actuators.
		getRoot().getChildren().clear();
		sensors.clear();
		actuators.clear();
	}

	private void addSensor(final int id, final SensorType sensorType, String name) {
		SensorCoapResource sensorResource = new SensorCoapResource(name, sensorType);

		// Register sensor resource on path /{sensors}/{id}/{sensor}.
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(sensorResource);
		sensorsResource.add(idResource);

		// Store reference to the resource in map.
		sensors.put(id, sensorResource);
	}

	private void addActuator(final int id, final ActuatorType actuatorType, String name) {
		ActuatorCoapResource actuatorResource = new ActuatorCoapResource(name, actuatorType) {

			@Override
			protected ResponseCode onSetValue(String value) throws DeviceServiceNotInitializedException {
				ResponseCode result = null;
				if (deviceService != null) {
					try {
						deviceService.setActuatorValue(id, value);
						result = ResponseCode.CHANGED;
					} catch (NoSuchActuatorException e) {
						LOGGER.log(Level.WARNING, e.getMessage());
						result = ResponseCode.SERVICE_UNAVAILABLE;
					}
				} else {
					throw new DeviceServiceNotInitializedException();
				}
				return result;
			}

			@Override
			protected ResponseCode onToggle() throws DeviceServiceNotInitializedException {
				ResponseCode result = null;
				if (deviceService != null) {
					try {
						deviceService.toggleActuator(id);
						result = ResponseCode.CHANGED;
					} catch (NoSuchActuatorException e) {
						LOGGER.log(Level.WARNING, e.getMessage());
						result = ResponseCode.SERVICE_UNAVAILABLE;
					}
				} else {
					throw new DeviceServiceNotInitializedException();
				}
				return result;
			}
		};

		// Register resource on path /{actuators}/{id}/{actuator}.
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(actuatorResource);
		actuatorsResource.add(idResource);

		// Store reference to the resource in map.
		actuators.put(id, actuatorResource);
	}

	/**
	 * Notifies given sensor resource about state change of the corresponding
	 * sensor on the device.
	 * 
	 * @param id
	 *            sensor id
	 * @param value
	 *            new value
	 */
	public void sensorChanged(Integer id, String value) {
		sensors.get(id).setValue(value);
	}

	/**
	 * Notifies given actuator resource about state change of the corresponding
	 * actuator on the device.
	 * 
	 * @param id
	 *            actuator id
	 * @param value
	 *            new value
	 */
	public void actuatorChanged(int id, String value) {
		actuators.get(id).setValue(value);
	}
}
