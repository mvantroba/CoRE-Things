package de.thk.rdw.endpoint.pi4j.osgi.resources;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import de.thk.rdw.base.ActuatorType;

public class LedResource extends ActuatorPi4jResource {

	private GpioController gpioController;
	private Pin anodePin;
	private String anodePinName;
	private GpioPinDigitalOutput anode;

	public LedResource(String name, ActuatorType actuatorType, GpioController gpioController, Pin anodePin) {
		super(name, actuatorType);
		this.gpioController = gpioController;
		this.anodePin = anodePin;
		this.anodePinName = String.format("%s-%s", name, "anode");
	}

	@Override
	public void init() {
		anode = gpioController.provisionDigitalOutputPin(anodePin, anodePinName, PinState.LOW);
		anode.setShutdownOptions(true, PinState.LOW);
	}

	@Override
	public void destroy() {
		anode.setState(PinState.LOW);
		gpioController.unprovisionPin(anode);
	}

	@Override
	public String getValue() {
		String result = null;
		if (anode != null) {
			result = anode.getState().toString();
		}
		return result;
	}

	@Override
	protected void toggle() {
		if (anode != null) {
			anode.toggle();
		}
	}
}
