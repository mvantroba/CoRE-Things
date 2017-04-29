package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;

import de.thk.rdw.base.SensorType;

public class MercurySwitchResource extends SensorGpioResource {

	public MercurySwitchResource(String name, GpioController gpioController, Pin pin) {
		super(name, SensorType.MERCURY_SWITCH, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalInput getInput() {
		GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, name);
		input.setShutdownOptions(true);
		return input;
	}
}
