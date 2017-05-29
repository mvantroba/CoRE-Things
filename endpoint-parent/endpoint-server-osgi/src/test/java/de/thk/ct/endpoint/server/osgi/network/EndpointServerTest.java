package de.thk.ct.endpoint.server.osgi.network;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.ct.endpoint.device.osgi.DeviceService;
import de.thk.ct.endpoint.device.osgi.resources.ActuatorType;
import de.thk.ct.endpoint.device.osgi.resources.ResourceType;
import de.thk.ct.endpoint.device.osgi.resources.SensorType;

public class EndpointServerTest {

	private EndpointServer endpointServer;
	private String endpointServerDiscoveryUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		endpointServer = new EndpointServer();
		endpointServer.addEndpoint(endpoint);
		endpointServer.start();
		// Obtain port after server has started.
		endpointServerDiscoveryUri = String.format("coap://localhost:%d/.well-known/core",
				endpoint.getAddress().getPort());
	}

	@After
	public void tearDown() throws Exception {
		if (endpointServer != null) {
			endpointServer.destroy();
		}
	}

	@Test
	public void When_NoDeviceService_Expect_MainResources() {
		CoapClient client = new CoapClient(endpointServerDiscoveryUri).useExecutor();

		String payload = client.get().getResponseText();

		// /.well-known/core + /s + /a
		Assert.assertEquals(3, LinkFormat.parse(payload).size());
		Assert.assertTrue(payload.contains(ResourceType.SENSOR.getName()));
		Assert.assertTrue(payload.contains(ResourceType.SENSOR.getType()));
		Assert.assertTrue(payload.contains(ResourceType.ACTUATOR.getName()));
		Assert.assertTrue(payload.contains(ResourceType.ACTUATOR.getType()));
	}

	@Test
	public void When_DeviceServiceWithSensors_Expect_SensorResources() {
		CoapClient client = new CoapClient(endpointServerDiscoveryUri).useExecutor();
		String name1 = "mercury1";
		String name2 = "mercury2";

		Map<Integer, Entry<String, String>> sensors = new TreeMap<>();
		sensors.put(0, new AbstractMap.SimpleEntry<>(SensorType.MERCURY_SWITCH.getType(), name1));
		sensors.put(1, new AbstractMap.SimpleEntry<>(SensorType.MERCURY_SWITCH.getType(), name2));

		DeviceService deviceService = mock(DeviceService.class);
		when(deviceService.getSensors()).thenReturn(sensors);
		endpointServer.setDeviceService(deviceService);

		String payload = client.get().getResponseText();

		// 3 main resources + 2 ID resources + 2 sensor resources.
		Assert.assertEquals(7, LinkFormat.parse(payload).size());
		Assert.assertTrue(payload.contains(name1));
		Assert.assertTrue(payload.contains(name2));
		Assert.assertTrue(payload.contains(SensorType.MERCURY_SWITCH.getType()));
	}

	@Test
	public void When_DeviceServiceWithActuators_Expect_ActuatorResources() {
		CoapClient client = new CoapClient(endpointServerDiscoveryUri).useExecutor();
		String name1 = "led1";
		String name2 = "led2";

		Map<Integer, Entry<String, String>> actuators = new TreeMap<>();
		actuators.put(0, new AbstractMap.SimpleEntry<>(ActuatorType.LED.getType(), name1));
		actuators.put(1, new AbstractMap.SimpleEntry<>(ActuatorType.LED.getType(), name2));

		DeviceService deviceService = mock(DeviceService.class);
		when(deviceService.getActuators()).thenReturn(actuators);
		endpointServer.setDeviceService(deviceService);

		String payload = client.get().getResponseText();

		// 3 main resources + 2 ID resources + 2 actuator resources.
		Assert.assertEquals(7, LinkFormat.parse(payload).size());
		Assert.assertTrue(payload.contains(name1));
		Assert.assertTrue(payload.contains(name2));
		Assert.assertTrue(payload.contains(ActuatorType.LED.getType()));
	}

	@Test
	public void When_UnsetDeviceService_Expect_NoSensorAndActuatorResources() {
		CoapClient client = new CoapClient(endpointServerDiscoveryUri).useExecutor();

		Map<Integer, Entry<String, String>> sensors = new TreeMap<>();
		sensors.put(0, new AbstractMap.SimpleEntry<>(SensorType.MERCURY_SWITCH.getType(), "mercury1"));
		sensors.put(1, new AbstractMap.SimpleEntry<>(SensorType.MERCURY_SWITCH.getType(), "mercury2"));

		Map<Integer, Entry<String, String>> actuators = new TreeMap<>();
		actuators.put(0, new AbstractMap.SimpleEntry<>(ActuatorType.LED.getType(), "led1"));
		actuators.put(1, new AbstractMap.SimpleEntry<>(ActuatorType.LED.getType(), "led2"));

		DeviceService deviceService = mock(DeviceService.class);
		when(deviceService.getSensors()).thenReturn(sensors);
		when(deviceService.getActuators()).thenReturn(actuators);
		endpointServer.setDeviceService(deviceService);

		Set<WebLink> links = LinkFormat.parse(client.get().getResponseText());

		// 3 main resources + 4 ID resources + 2 sensor resources + 2 actuator
		// resources.
		Assert.assertEquals(11, links.size());

		endpointServer.unsetDeviceService();

		String payload = client.get().getResponseText();

		// /.well-known/core + /s + /a
		Assert.assertEquals(3, LinkFormat.parse(payload).size());
		Assert.assertTrue(payload.contains(ResourceType.SENSOR.getName()));
		Assert.assertTrue(payload.contains(ResourceType.SENSOR.getType()));
		Assert.assertTrue(payload.contains(ResourceType.ACTUATOR.getName()));
		Assert.assertTrue(payload.contains(ResourceType.ACTUATOR.getType()));
	}
}
