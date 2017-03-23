package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private GpioController gpioController;
	private GpioPinDigitalOutput led;
	private GpioPinDigitalInput tilt;

	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();
		led = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "led", PinState.LOW);
		led.setShutdownOptions(true, PinState.LOW);
		tilt = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_28, "tilt");
		tilt.setShutdownOptions(true);
	}

	@Override
	public void toggleActuator(String name) {
		GpioPin pin = gpioController.getProvisionedPin(name);
		if (pin != null) {
			((GpioPinDigitalOutput) pin).toggle();
			LOGGER.log(Level.INFO, "Toggled actuator with name \"{0}\".", new Object[] { name });
			LOGGER.log(Level.INFO, "Tilt state: \"{0}\"", new Object[] { tilt.getState().getName() });
		} else {
			LOGGER.log(Level.WARNING, "Actuator with name \"{0}\" does not exists.", new Object[] { name });
		}

	}

	public void deactivate() {
		led.setState(PinState.LOW);
		gpioController.shutdown();
		gpioController.unprovisionPin(led);
		gpioController.unprovisionPin(tilt);
		LOGGER.log(Level.INFO, "GPIO controller has been shut down.");
	}
}
