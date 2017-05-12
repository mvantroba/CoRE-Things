package de.thk.ct.endpoint.pi4j.osgi.resources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import de.thk.ct.endpoint.pi4j.osgi.resources.gpio.LedResource;
import de.thk.ct.endpoint.pi4j.osgi.resources.gpio.MercurySwitchResource;
import de.thk.ct.endpoint.pi4j.osgi.resources.gpio.PirSensorResource;
import de.thk.ct.endpoint.pi4j.osgi.resources.gpio.TactileSwitchResource;

/**
 * Factory class for all resources in this application. Created resource will be
 * either of type {@link SensorResource} or {@link ActuatorResource}.
 * 
 * @author Martin Vantroba
 *
 */
public class DeviceResourceFactory {

	private static final Logger LOGGER = Logger.getLogger(DeviceResourceFactory.class.getName());

	private DeviceResourceFactory() {
	}

	// ####################
	// SENSORS
	// ####################

	/**
	 * Constructs {@link MercurySwitchResource} if all arguments are valid,
	 * otherwise it returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param gpioController
	 *            controller
	 * @param pinString
	 *            pin number as string
	 * @return created resource
	 */
	public static SensorResource createMercurySwitch(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new MercurySwitchResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	/**
	 * Constructs {@link TactileSwitchResource} if all arguments are valid,
	 * otherwise it returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param gpioController
	 *            controller
	 * @param pinString
	 *            pin number as string
	 * @return created resource
	 */
	public static SensorResource createTactileSwitch(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new TactileSwitchResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	/**
	 * Constructs {@link PirSensorResource} if all arguments are valid,
	 * otherwise it returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param gpioController
	 *            controller
	 * @param pinString
	 *            pin number as string
	 * @return created resource
	 */
	public static SensorResource createPirSensor(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new PirSensorResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	/**
	 * Constructs {@link DS18B20Resource} if all arguments are valid, otherwise
	 * it returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param filename
	 *            file where temperature value is stored
	 * @param threshold
	 *            value threshold for listener
	 * @return created resource
	 */
	public static SensorResource createDS18B20(String name, DeviceResourceListener listener, String filename,
			String threshold) {

		SensorResource result = null;
		double thresholdNumber;
		try {
			validateFileParameters(name, filename);
			thresholdNumber = parseThreshold(threshold);
			if (thresholdNumber > 0) {
				result = new DS18B20Resource(name, listener, filename, thresholdNumber);
			} else {
				result = new DS18B20Resource(name, listener, filename);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create file resource [name={0},filename={1}]. {2}",
					new Object[] { name, filename, e.getMessage() });
		}

		return result;
	}

	// ####################
	// ACTUATORS
	// ####################

	/**
	 * Constructs {@link LedResource} if all arguments are valid, otherwise it
	 * returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param gpioController
	 *            controller
	 * @param pinString
	 *            pin number as string
	 * @return created resource
	 */
	public static ActuatorResource createLed(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		ActuatorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new LedResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	/**
	 * Constructs {@link Lcd16x2Resource} if all arguments are valid, otherwise
	 * it returns null.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param bus
	 *            assigned I2C bus
	 * @param deviceAddress
	 *            hexadecimal device address as string
	 * @return created resource
	 */
	public static ActuatorResource createLcd16x2(String name, DeviceResourceListener listener, I2CBus bus,
			String deviceAddress) {

		ActuatorResource result = null;
		try {
			byte deviceAddressBits = Byte.decode(deviceAddress);
			if (bus != null) {
				I2CDevice device = bus.getDevice(deviceAddressBits);
				result = new Lcd16x2Resource(name, listener, device);
			} else {
				LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},deviceAddress={1}]. {2} is null.",
						new Object[] { name, deviceAddress, I2CBus.class.getName() });
			}
		} catch (NumberFormatException | IOException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},deviceAddress={1}]. {2}.",
					new Object[] { name, deviceAddress, e.getMessage() });
		}
		return result;
	}

	private static void validateGpioParameters(String name, GpioController gpioController, Pin pin) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Resource name is null or empty.");
		}
		if (gpioController == null) {
			throw new IllegalArgumentException("GPIO controller is null.");
		}
		if (pin == null) {
			throw new IllegalArgumentException("Pin is invalid.");
		}
	}

	private static void validateFileParameters(String name, String filename) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Resource name is null or empty.");
		}
		if (filename == null || filename.isEmpty()) {
			throw new IllegalArgumentException("Filename name is null or empty.");
		}
	}

	private static double parseThreshold(String threshold) {
		double result = -1;
		try {
			result = Double.parseDouble(threshold);
		} catch (NumberFormatException e) {
			LOGGER.log(Level.WARNING, "Could not parse threshold value \"{0}\" to double.", new Object[] { threshold });
		}
		return result;
	}

	private static Pin parsePin(String pin) {
		Pin result = null;
		try {
			int pinNr = Integer.parseInt(pin);
			result = RaspiPin.getPinByAddress(pinNr);
		} catch (NumberFormatException e) {
			// Do nothing.
		}
		return result;
	}
}
