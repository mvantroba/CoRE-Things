package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.endpoint.pi4j.osgi.resources.DeviceResourceListener;

/**
 * The type of {@link ActuatorGpioResource} that represents a simple LED.
 * 
 * @author Martin Vantroba
 *
 */
public class LedResource extends ActuatorGpioResource {

	/**
	 * Constructs a {@link LedResource} which will be managed by the given
	 * {@link GpioController}.
	 * 
	 * @param name
	 *            actuator name
	 * @param listener
	 *            actuator listener
	 * @param gpioController
	 *            controller
	 * @param pin
	 *            pin which this actuator is associated with
	 */
	public LedResource(String name, DeviceResourceListener listener, GpioController gpioController, Pin pin) {
		super(name, listener, ActuatorType.LED, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalOutput getOutput() {
		GpioPinDigitalOutput output = gpioController.provisionDigitalOutputPin(pin, name, PinState.LOW);
		output.setShutdownOptions(true, PinState.LOW);
		return output;
	}
}
