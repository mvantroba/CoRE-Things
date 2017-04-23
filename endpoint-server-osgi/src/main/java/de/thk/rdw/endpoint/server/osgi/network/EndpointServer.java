package de.thk.rdw.endpoint.server.osgi.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;

import de.thk.rdw.base.ActuatorResourceType;
import de.thk.rdw.base.SensorResourceType;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.server.osgi.DeviceServiceNotInitializedException;
import de.thk.rdw.endpoint.server.osgi.ResourceProfile;
import de.thk.rdw.endpoint.server.osgi.resource.ActuatorCoapResource;
import de.thk.rdw.endpoint.server.osgi.resource.SensorCoapResource;

public class EndpointServer extends CoapServer {

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
		actuatorsResource = new CoapResource(ResourceProfile.ACTUATORS.getName());
		actuatorsResource.getAttributes().addResourceType(ResourceProfile.ACTUATORS.getResourceType());
		sensorsResource = new CoapResource(ResourceProfile.SENSORS.getName());
		sensorsResource.getAttributes().addResourceType(ResourceProfile.SENSORS.getResourceType());
		add(actuatorsResource, sensorsResource);

		addActuator(ActuatorResourceType.LED);

		addSensor(SensorResourceType.TILT);
		addSensor(SensorResourceType.PUSH);
		addSensor(SensorResourceType.MOTION);
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void unsetDeviceService() {
		this.deviceService = null;
	}

	public void addActuator(final ActuatorResourceType actuatorResourceType) {
		int id;
		if (!actuators.isEmpty()) {
			id = actuators.lastKey() + 1;
		} else {
			id = 0;
		}
		ActuatorCoapResource actuatorResource = new ActuatorCoapResource(actuatorResourceType.getName()) {

			@Override
			protected void onToggle() throws DeviceServiceNotInitializedException {
				if (deviceService != null) {
					deviceService.toggleActuator(actuatorResourceType.getName());
				} else {
					throw new DeviceServiceNotInitializedException();
				}
			}
		};
		actuatorResource.getAttributes().addResourceType(actuatorResourceType.getResourceType());
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(actuatorResource);
		// Register resource in path /{actuators}/{id}/{actuator}.
		actuatorsResource.add(idResource);
		// Store reference to the resource in map.
		actuators.put(id, actuatorResource);
	}

	public void addSensor(final SensorResourceType sensorResourceType) {
		int id;
		if (!sensors.isEmpty()) {
			id = sensors.lastKey() + 1;
		} else {
			id = 0;
		}
		SensorCoapResource sensorResource = new SensorCoapResource(sensorResourceType.getName()) {

			@Override
			protected String onGetValue() {
				// TODO Implement this method.
				return null;
			}
		};
		sensorResource.getAttributes().addResourceType(sensorResourceType.getResourceType());
		CoapResource idResource = new CoapResource(String.valueOf(id));
		idResource.add(sensorResource);
		// Register resource in path /{actuators}/{id}/{actuator}.
		sensorsResource.add(idResource);
		// Store reference to the resource in map.
		sensors.put(id, sensorResource);
	}

	public void sensorChanged(Integer id, String newValue) {
		sensors.get(id).setSensorValue(newValue);
	}
}
