package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.pi4j.osgi.resources.DeviceResourceListener;

public class PirSensorResource extends SensorGpioResource {

	public PirSensorResource(String name, DeviceResourceListener listener, GpioController gpioController, Pin pin) {
		super(name, listener, SensorType.PIR_SENSOR, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalInput getInput() {
		GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, name, PinPullResistance.PULL_UP);
		input.setShutdownOptions(true);
		return input;
	}
}
