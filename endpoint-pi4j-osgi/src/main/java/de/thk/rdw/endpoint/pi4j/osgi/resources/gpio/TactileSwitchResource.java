package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

import de.thk.rdw.base.SensorType;

public class TactileSwitchResource extends SensorGpioResource {

	public TactileSwitchResource(String name, GpioController gpioController, Pin pin) {
		super(name, SensorType.TACTILE_SWITCH, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalInput getInput() {
		GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, name, PinPullResistance.PULL_DOWN);
		input.setShutdownOptions(true);
		return input;
	}
}
