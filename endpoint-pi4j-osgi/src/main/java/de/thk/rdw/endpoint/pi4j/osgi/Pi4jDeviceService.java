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
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.device.osgi.NoSuchActuatorException;
import de.thk.rdw.endpoint.device.osgi.NoSuchSensorException;
import de.thk.rdw.endpoint.pi4j.osgi.data.CsvReader;
import de.thk.rdw.endpoint.pi4j.osgi.resources.ActuatorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.SensorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.LedResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.MercurySwitchResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.PirSensorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.TactileSwitchResource;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private List<DeviceListener> deviceListeners = new ArrayList<>();

	private NavigableMap<Integer, SensorResource> sensors = new TreeMap<>();
	private NavigableMap<Integer, ActuatorResource> actuators = new TreeMap<>();

	private GpioController gpioController;

	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();

		CsvReader.read();

		addActuator("led", ActuatorType.LED, RaspiPin.GPIO_00);

		addSensor("tilt", SensorType.MERCURY_SWITCH, RaspiPin.GPIO_01);
		addSensor("button", SensorType.TACTILE_SWITCH, RaspiPin.GPIO_02);
		addSensor("motion", SensorType.PIR_SENSOR, RaspiPin.GPIO_03);
	}

	@Override
	public boolean addListener(DeviceListener deviceListener) {
		return deviceListeners.add(deviceListener);
	}

	@Override
	public boolean removeListener(DeviceListener deviceListener) {
		return deviceListeners.remove(deviceListener);
	}

	@Override
	public void toggleActuator(int id) throws NoSuchActuatorException {
		ActuatorResource actuator = actuators.get(id);
		if (actuator != null) {
			actuator.toggle();
			LOGGER.log(Level.INFO, "Toggled \"{0}\".", new Object[] { actuator.toString() });
		} else {
			throw new NoSuchActuatorException(
					String.format("Actuator with ID \"%d\" does not exists on the device.", id));
		}
	}

	@Override
	public NavigableMap<Integer, Entry<SensorType, String>> getSensors() {
		NavigableMap<Integer, Entry<SensorType, String>> result = new TreeMap<>();
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			SensorResource sensor = entry.getValue();
			result.put(entry.getKey(), new AbstractMap.SimpleEntry<>(sensor.getSensorType(), sensor.getName()));
		}
		return result;
	}

	@Override
	public Map<Integer, Entry<ActuatorType, String>> getActuators() {
		NavigableMap<Integer, Entry<ActuatorType, String>> result = new TreeMap<>();
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			ActuatorResource actuator = entry.getValue();
			result.put(entry.getKey(), new AbstractMap.SimpleEntry<>(actuator.getActuatorType(), actuator.getName()));
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
			throw new NoSuchSensorException(String.format("Sensor with ID %d does not exists on the device.", id));
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
			throw new NoSuchActuatorException(String.format("Actuator with ID %d does not exists on the device.", id));
		}
		return result;
	}

	@Override
	public void setActuatorValue(int id, String value) throws NoSuchActuatorException {
		ActuatorResource actuator = actuators.get(id);
		if (actuator != null) {
			actuator.toggle();
			LOGGER.log(Level.INFO, "Set {0} to value {1}.", new Object[] { actuator.toString(), value });
		} else {
			throw new NoSuchActuatorException(String.format("Actuator with ID %d does not exists on the device.", id));
		}
	}

	public void init() {
		int counter = 0;
		// Provision all sensor pins.
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			entry.getValue().init();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} sensors have been initialised.", new Object[] { counter });
		// Povision all actuator pins.
		counter = 0;
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			entry.getValue().init();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} actuators have been initialised.", new Object[] { counter });
	}

	public void destroy() {
		int counter = 0;
		// Unprovision all sensor pins.
		for (Entry<Integer, SensorResource> entry : sensors.entrySet()) {
			entry.getValue().destroy();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} sensors have been destroyed.", new Object[] { counter });
		counter = 0;
		// Unprovision all actuator pins.
		for (Entry<Integer, ActuatorResource> entry : actuators.entrySet()) {
			entry.getValue().destroy();
			counter++;
		}
		LOGGER.log(Level.INFO, "{0} actuators have been destroyed.", new Object[] { counter });

		// Calling GpioController.shutdown() causes
		// java.util.concurrent.RejectedExecutionException in
		// listeners after bundle is started again.

	}

	private void addSensor(String name, SensorType sensorType, Pin pin) {
		final int id;
		if (!sensors.isEmpty()) {
			id = sensors.lastKey() + 1;
		} else {
			id = 0;
		}
		switch (sensorType) {
		case MERCURY_SWITCH:
			MercurySwitchResource mercurySwitchResource = new MercurySwitchResource(name, gpioController, pin);
			mercurySwitchResource.setListener(createSensorGpioPinListener(id));
			sensors.put(id, mercurySwitchResource);
			LOGGER.log(Level.INFO, "Added {0}. ID: {1}.", new Object[] { mercurySwitchResource.toString(), id });
			break;
		case TACTILE_SWITCH:
			TactileSwitchResource tactileSwitchResource = new TactileSwitchResource(name, gpioController, pin);
			tactileSwitchResource.setListener(createSensorGpioPinListener(id));
			sensors.put(id, tactileSwitchResource);
			LOGGER.log(Level.INFO, "Added {0}. ID: {1}.", new Object[] { tactileSwitchResource.toString(), id });
			break;
		case PIR_SENSOR:
			PirSensorResource pirSensorResource = new PirSensorResource(name, gpioController, pin);
			pirSensorResource.setListener(createSensorGpioPinListener(id));
			sensors.put(id, pirSensorResource);
			LOGGER.log(Level.INFO, "Added {0}. ID: {1}.", new Object[] { pirSensorResource.toString(), id });
			break;
		default:
			break;
		}
	}

	private void addActuator(String name, ActuatorType actuatorType, Pin pin) {
		final int id;
		if (!actuators.isEmpty()) {
			id = actuators.lastKey() + 1;
		} else {
			id = 0;
		}
		switch (actuatorType) {
		case LED:
			LedResource ledResource = new LedResource(name, gpioController, pin);
			ledResource.setListener(createActuatorGpioPinListener(id));
			actuators.put(id, ledResource);
			LOGGER.log(Level.INFO, "Added {0}. ID: {1}.", new Object[] { ledResource.toString(), id });
			break;
		default:
			break;
		}
	}

	private GpioPinListener createSensorGpioPinListener(final int id) {
		return new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				for (DeviceListener deviceListener : deviceListeners) {
					deviceListener.onSensorChanged(id, event.getState().getName());
				}
				LOGGER.log(Level.INFO, "Sensor with ID \"{0}\" changed state to \"{1}\".",
						new Object[] { id, event.getState().getName() });
			}
		};
	}

	private GpioPinListener createActuatorGpioPinListener(final int id) {
		return new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				for (DeviceListener deviceListener : deviceListeners) {
					deviceListener.onActuatorChanged(id, event.getState().getName());
				}
				LOGGER.log(Level.INFO, "Actuator with ID \"{0}\" changed state to \"{1}\".",
						new Object[] { id, event.getState().getName() });
			}
		};
	}
}
