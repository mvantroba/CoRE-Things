package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private List<DeviceListener> deviceListeners = new ArrayList<>();

	private GpioController gpioController;
	private GpioPinDigitalOutput led;
	private GpioPinDigitalInput tilt;

	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();
		led = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, "led", PinState.LOW);
		led.setShutdownOptions(true, PinState.LOW);
		tilt = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01, "tilt");
		tilt.setShutdownOptions(true);
		tilt.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				LOGGER.log(Level.INFO, "Tilt state changed: \"{0}\"", event.getState().getName());
				notifyListeners("tilt", event.getState().getName());
			}
		});
	}

	@Override
	public boolean addListener(DeviceListener deviceListener) {
		return deviceListeners.add(deviceListener);
	}

	@Override
	public boolean removeListener(DeviceListener deviceListener) {
		return deviceListeners.remove(deviceListener);
	}

	@Override
	public void toggleActuator(String name) {
		GpioPin pin = gpioController.getProvisionedPin(name);
		if (pin != null) {
			((GpioPinDigitalOutput) pin).toggle();
			LOGGER.log(Level.INFO, "Toggled actuator with name \"{0}\".", new Object[] { name });
		} else {
			LOGGER.log(Level.WARNING, "Actuator with name \"{0}\" does not exists.", new Object[] { name });
		}
	}

	public void deactivate() {
		led.setState(PinState.LOW);

		// shutdown() causes java.util.concurrent.RejectedExecutionException in
		// listeners after bundle is started again.
		// gpioController.shutdown();

		gpioController.unprovisionPin(led);
		gpioController.unprovisionPin(tilt);
		LOGGER.log(Level.INFO, "All GPIO pins has been unprovisioned.");
	}

	private void notifyListeners(String name, Object newValue) {
		for (DeviceListener deviceListener : deviceListeners) {
			deviceListener.onSensorChanged(name, newValue);
		}
	}
}
