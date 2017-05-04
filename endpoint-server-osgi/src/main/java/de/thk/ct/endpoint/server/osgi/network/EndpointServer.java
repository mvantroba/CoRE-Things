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
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

import de.thk.ct.base.ActuatorType;
import de.thk.ct.base.ResourceType;
import de.thk.ct.base.SensorType;
import de.thk.ct.endpoint.device.osgi.DeviceService;
import de.thk.ct.endpoint.device.osgi.NoSuchActuatorException;
import de.thk.ct.endpoint.device.osgi.NoSuchSensorException;
import de.thk.ct.endpoint.server.osgi.DeviceServiceNotInitializedException;
import de.thk.ct.endpoint.server.osgi.resource.ActuatorCoapResource;
import de.thk.ct.endpoint.server.osgi.resource.SensorCoapResource;

public class EndpointServer extends CoapServer {

	private static final Logger LOGGER = Logger.getLogger(EndpointServer.class.getName());

	private DeviceService deviceService;

	private CoapResource actuatorsResource;
	private CoapResource sensorsResource;

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
		actuatorsResource = new CoapResource(ResourceType.ACTUATOR.getName());
		actuatorsResource.getAttributes().addResourceType(ResourceType.ACTUATOR.getType());
		sensorsResource = new CoapResource(ResourceType.SENSOR.getName());
		sensorsResource.getAttributes().addResourceType(ResourceType.SENSOR.getType());
		add(actuatorsResource, sensorsResource);
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
		for (Entry<Integer, Entry<String, String>> entry : deviceService.getSensors().entrySet()) {
			Entry<String, String> sensor = entry.getValue();
			SensorType sensorType = SensorType.get(sensor.getKey());
			if (sensorType != null) {
				addSensor(entry.getKey(), sensorType, sensor.getValue());
			} else {
				LOGGER.log(Level.WARNING, "Sensor type \"{0}\" is unknown.", sensor.getKey());
			}
		}
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

	public void unsetDeviceService() {
		this.deviceService = null;
		// Clear all sensors and actuators.
		getRoot().getChildren().clear();
		sensors.clear();
		actuators.clear();

	}

	public void addSensor(final int id, final SensorType sensorType, String name) {
		SensorCoapResource sensorResource = new SensorCoapResource(name, sensorType) {

			@Override
			protected String onGetValue() throws DeviceServiceNotInitializedException {
				String result = null;
				if (deviceService != null) {
					try {
						result = deviceService.getSensorValue(id);
					} catch (NoSuchSensorException e) {
						LOGGER.log(Level.WARNING, e.getMessage());
					}
				} else {
					throw new DeviceServiceNotInitializedException();
				}
				return result;
			}
		};
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(sensorResource);
		// Register resource in path /{sensors}/{id}/{sensor}.
		sensorsResource.add(idResource);
		// Store reference to the resource in map.
		sensors.put(id, sensorResource);
	}

	public void addActuator(final int id, final ActuatorType actuatorType, String name) {
		ActuatorCoapResource actuatorResource = new ActuatorCoapResource(name, actuatorType) {

			@Override
			protected void onToggle() throws DeviceServiceNotInitializedException {
				if (deviceService != null) {
					try {
						deviceService.toggleActuator(id);
					} catch (NoSuchActuatorException e) {
						LOGGER.log(Level.WARNING, e.getMessage());
					}
				} else {
					throw new DeviceServiceNotInitializedException();
				}
			}
		};
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(actuatorResource);
		// Register resource in path /{actuators}/{id}/{actuator}.
		actuatorsResource.add(idResource);
		// Store reference to the resource in map.
		actuators.put(id, actuatorResource);
	}

	public void sensorChanged(Integer id, String value) {
		sensors.get(id).setValue(value);
	}

	public void actuatorChanged(int id, String value) {
		actuators.get(id).setValue(value);
	}
}
