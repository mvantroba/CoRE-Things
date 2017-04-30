package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import de.thk.rdw.base.ActuatorType;

public class LedResource extends ActuatorGpioResource {

	public LedResource(String name, GpioController gpioController, Pin pin) {
		super(name, ActuatorType.LED, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalOutput getOutput() {
		GpioPinDigitalOutput output = gpioController.provisionDigitalOutputPin(pin, name, PinState.LOW);
		output.setShutdownOptions(true, PinState.LOW);
		return output;
	}
}