package de.thk.rdw.endpoint.pi4j.osgi.resources;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListener;

import de.thk.rdw.base.SensorType;

public class TactileSwitchResource extends SensorPi4jResource {

	private GpioController gpioController;
	private Pin signalPin;
	private String signalPinName;
	private GpioPinDigitalInput signal;

	public TactileSwitchResource(String name, SensorType sensorType, GpioController gpioController, Pin signalPin) {
		super(name, sensorType);
		this.gpioController = gpioController;
		this.signalPin = signalPin;
		this.signalPinName = String.format("%s-%s", name, "signal");
	}

	@Override
	public void init() {
		signal = gpioController.provisionDigitalInputPin(signalPin, signalPinName, PinPullResistance.PULL_DOWN);
		signal.setShutdownOptions(true);
	}

	@Override
	public void destroy() {
		gpioController.unprovisionPin(signal);
	}

	@Override
	public String getValue() {
		String result = null;
		if (signal != null) {
			result = signal.getState().toString();
		}
		return result;
	}

	public void addListener(GpioPinListener gpioPinListener) {
		if (signal != null) {
			signal.addListener(gpioPinListener);
		}
	}
}
