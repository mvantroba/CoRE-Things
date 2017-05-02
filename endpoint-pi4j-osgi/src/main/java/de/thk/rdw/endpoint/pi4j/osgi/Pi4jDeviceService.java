package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.ResourceType;
import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.device.osgi.NoSuchActuatorException;
import de.thk.rdw.endpoint.device.osgi.NoSuchSensorException;
import de.thk.rdw.endpoint.pi4j.osgi.data.CsvReader;
import de.thk.rdw.endpoint.pi4j.osgi.resources.ActuatorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.DeviceResourceFactory;
import de.thk.rdw.endpoint.pi4j.osgi.resources.DeviceResourceListener;
import de.thk.rdw.endpoint.pi4j.osgi.resources.SensorResource;

/**
 * The {@link DeviceService} type which enables access to Raspberry Pi sensors
 * and actuators using the Pi4J framework. Sensors and actuators are obtained
 * from files using {@link CsvReader} in constructor of this class.
 * <p>
 * The {@link Pi4jDeviceService #init()} method initializes all resources by
 * provisioning pins. The {@link Pi4jDeviceService #destroy()} should be called
 * to destroy all resources after before application exits.
 * 
 * @author Martin Vantroba
 *
 */
public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private GpioController gpioController;

	private NavigableMap<Integer, SensorResource> sensors = new TreeMap<>();
	private NavigableMap<Integer, ActuatorResource> actuators = new TreeMap<>();

	private List<DeviceListener> deviceListeners = new ArrayList<>();

	/**
	 * Constructs the {@link Pi4jDeviceService} and reads all sensors and
	 * actuators from the file using {@link CsvReader}. These sensors have to be
	 * initialized by calling the {@link Pi4jDeviceService #init()} method
	 * before it can be used.
	 */
	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();
		for (String[] sensorArguments : CsvReader.readResources(ResourceType.SENSOR)) {
			addSensor(sensorArguments);
		}
		for (String[] actuatorArguments : CsvReader.readResources(ResourceType.ACTUATOR)) {
			addActuator(actuatorArguments);
		}
	}

	@Override
	public NavigableMap<Integer, Entry<String, String>> getSensors() {
		NavigableMap<Integer, Entry<String, String>> result = new TreeMap<>();
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			SensorResource sensor = entry.getValue();
			result.put(entry.getKey(),
					new AbstractMap.SimpleEntry<>(sensor.getSensorType().getType(), sensor.getName()));
		}
		return result;
	}

	@Override
	public Map<Integer, Entry<String, String>> getActuators() {
		NavigableMap<Integer, Entry<String, String>> result = new TreeMap<>();
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			ActuatorResource actuator = entry.getValue();
			result.put(entry.getKey(),
					new AbstractMap.SimpleEntry<>(actuator.getActuatorType().getType(), actuator.getName()));
		}
		return result;
	}

	@Override
	public String getSensorValue(int id) throws NoSuchSensorException {
		String result = null;
		SensorResource sensor = sensors.get(id);
		if (sensor != null) {
			result = sensor.getValue();
		} else {
			throw new NoSuchSensorException(id);
		}
		return result;
	}

	@Override
	public String getActuatorValue(int id) throws NoSuchActuatorException {
		String result = null;
		ActuatorResource actuator = actuators.get(id);
		if (actuator != null) {
			result = actuator.getValue();
		} else {
			throw new NoSuchActuatorException(id);
		}
		return result;
	}

	@Override
	public void setActuatorValue(int id, String value) throws NoSuchActuatorException {
		ActuatorResource actuator = actuators.get(id);
		if (actuator != null) {
			try {
				actuator.setValue(value);
				LOGGER.log(Level.INFO, "Set {0} to value {1}.", new Object[] { actuator.toString(), value });
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		} else {
			throw new NoSuchActuatorException(id);
		}
	}

	@Override
	public void toggleActuator(int id) throws NoSuchActuatorException {
		ActuatorResource actuator = actuators.get(id);
		if (actuator != null) {
			actuator.toggle();
			LOGGER.log(Level.INFO, "Toggled {0}.", new Object[] { actuator.toString() });
		} else {
			throw new NoSuchActuatorException(id);
		}
	}

	@Override
	public boolean addListener(DeviceListener deviceListener) {
		return deviceListeners.add(deviceListener);
	}

	@Override
	public boolean removeListener(DeviceListener deviceListener) {
		return deviceListeners.remove(deviceListener);
	}

	/**
	 * Initializes all sensors and actuators.
	 */
	public void init() {
		int counter = 0;
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			entry.getValue().init();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} sensors have been initialised.", new Object[] { counter });
		counter = 0;
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			entry.getValue().init();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} actuators have been initialised.", new Object[] { counter });
	}

	/**
	 * Destroys all sensors and actuators.
	 */
	public void destroy() {
		int counter = 0;
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			entry.getValue().destroy();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} sensors have been destroyed.", new Object[] { counter });
		counter = 0;
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			entry.getValue().destroy();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} actuators have been destroyed.", new Object[] { counter });

		// Calling GpioController.shutdown() causes RejectedExecutionException
		// in listeners after bundle is started again.
	}

	private void addSensor(String[] arguments) {
		SensorResource sensorResource = null;
		final int id = !sensors.isEmpty() ? sensors.lastKey() + 1 : 0;
		String name = arguments[0];
		String type = arguments[1];

		DeviceResourceListener listener = new DeviceResourceListener() {

			@Override
			public void onChanged(String value) {
				LOGGER.log(Level.INFO, "Sensor with ID {0} changed value to {1}.", new Object[] { id, value });
				// Notify all listeners.
				for (DeviceListener deviceListener : deviceListeners) {
					deviceListener.onSensorChanged(id, value);
				}
			}
		};

		switch (SensorType.get(type)) {
		case MERCURY_SWITCH:
			sensorResource = DeviceResourceFactory.createMercurySwitch(name, listener, gpioController, arguments[2]);
			break;
		case TACTILE_SWITCH:
			sensorResource = DeviceResourceFactory.createTactileSwitch(name, listener, gpioController, arguments[2]);
			break;
		case PIR_SENSOR:
			sensorResource = DeviceResourceFactory.createPirSensor(name, listener, gpioController, arguments[2]);
			break;
		default:
			break;
		}

		if (sensorResource != null) {
			sensors.put(id, sensorResource);
			LOGGER.log(Level.INFO, "Added {0} with ID {1}.", new Object[] { sensorResource.toString(), id });
		}
	}

	private void addActuator(String[] arguments) {
		ActuatorResource actuatorResource = null;
		final int id = !actuators.isEmpty() ? actuators.lastKey() + 1 : 0;
		String name = arguments[0];
		String type = arguments[1];

		DeviceResourceListener listener = new DeviceResourceListener() {

			@Override
			public void onChanged(String value) {
				LOGGER.log(Level.INFO, "Actuator with ID {0} changed value to {1}.", new Object[] { id, value });
				for (DeviceListener deviceListener : deviceListeners) {
					// Notify all listeners.
					deviceListener.onActuatorChanged(id, value);
				}
			}
		};

		switch (ActuatorType.get(type)) {
		case LED:
			actuatorResource = DeviceResourceFactory.createLed(name, listener, gpioController, arguments[2]);
			break;
		default:
			break;
		}

		if (actuatorResource != null) {
			actuators.put(id, actuatorResource);
			LOGGER.log(Level.INFO, "Added {0} with ID {1}.", new Object[] { actuatorResource.toString(), id });
		}
	}
}
