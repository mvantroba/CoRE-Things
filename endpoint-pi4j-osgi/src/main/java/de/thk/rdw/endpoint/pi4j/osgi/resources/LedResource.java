package de.thk.rdw.endpoint.pi4j.osgi.resources;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import de.thk.rdw.base.ActuatorType;

public class LedResource extends ActuatorPi4jResource {

	private static final String ANODE_PIN_NAME = "anode";

	private GpioController gpioController;
	private Pin anodePin;
	private GpioPinDigitalOutput anode;

	public LedResource(String name, ActuatorType actuatorType, GpioController gpioController, Pin anodePin) {
		super(name, actuatorType);
		this.gpioController = gpioController;
		this.anodePin = anodePin;
	}

	@Override
	public void init() {
		anode = gpioController.provisionDigitalOutputPin(anodePin, ANODE_PIN_NAME, PinState.LOW);
		anode.setShutdownOptions(true, PinState.LOW);
	}

	@Override
	public void destroy() {
		anode.setState(PinState.LOW);
		gpioController.unprovisionPin(anode);
	}
}
