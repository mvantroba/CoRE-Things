package de.thk.ct.endpoint.pi4j.osgi.resources.gpio;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.ct.endpoint.device.osgi.resources.SensorType;
import de.thk.ct.endpoint.pi4j.osgi.resources.DeviceResourceListener;
import de.thk.ct.endpoint.pi4j.osgi.resources.SensorResource;

/**
 * The type of {@link SensorResource} which uses GPIO pins and
 * {@link GpioController}.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class SensorGpioResource extends SensorResource {

	private static final Logger LOGGER = Logger.getLogger(SensorGpioResource.class.getName());

	protected GpioController gpioController;
	protected Pin pin;

	private GpioPinDigitalInput input;
	private GpioPinListener gpioPinListener;

	/**
	 * Constructs a {@link SensorGpioResource} which will be managed by the
	 * given {@link GpioController}.
	 * 
	 * @param name
	 *            sensor name
	 * @param listener
	 *            sensor listener
	 * @param sensorType
	 *            sensor type
	 * @param gpioController
	 *            controller
	 * @param pin
	 *            pin which this sensor is associated with
	 */
	public SensorGpioResource(String name, DeviceResourceListener listener, SensorType sensorType,
			GpioController gpioController, Pin pin) {
		super(name, listener, sensorType);
		this.gpioController = gpioController;
		this.pin = pin;
		this.gpioPinListener = new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				onChanged(event.getState().toString());
			}
		};
	}

	/**
	 * Returns input pin interface, that will be used to obtain state of this
	 * resource.
	 * 
	 * @return input pin interface
	 */
	protected abstract GpioPinDigitalInput getInput();

	@Override
	public String toString() {
		return "Sensor [pin=" + pin + ", name=" + name + ", sensorType=" + sensorType + "]";
	}

	@Override
	public void init() {
		input = getInput();
		if (gpioPinListener != null) {
			input.addListener(gpioPinListener);
		} else {
			LOGGER.log(Level.WARNING, "Could not add pin listener to {0}, because it is not initialized.",
					new Object[] { toString() });
		}
		LOGGER.log(Level.INFO, "Initialized {0}.", new Object[] { toString() });
	}

	@Override
	public void destroy() {
		input.removeAllListeners();
		gpioController.unprovisionPin(input);
		LOGGER.log(Level.INFO, "Destroyed {0}.", new Object[] { toString() });
	}

	@Override
	public String getValue() {
		String result = null;
		if (input != null) {
			result = input.getState().toString();
		} else {
			LOGGER.log(Level.WARNING, "Could not read value of {0}, because pin is not provisioned.",
					new Object[] { toString() });
		}
		return result;
	}
}
