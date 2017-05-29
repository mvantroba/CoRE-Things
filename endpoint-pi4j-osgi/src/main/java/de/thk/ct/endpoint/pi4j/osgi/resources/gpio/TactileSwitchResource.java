package de.thk.ct.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

import de.thk.ct.endpoint.device.osgi.resources.SensorType;
import de.thk.ct.endpoint.pi4j.osgi.resources.DeviceResourceListener;

/**
 * The type of {@link SensorGpioResource} that represents tactile switch.
 * 
 * @author Martin Vantroba
 *
 */
public class TactileSwitchResource extends SensorGpioResource {

	/**
	 * Constructs a {@link TactileSwitchResource} which will be managed by the
	 * given {@link GpioController}.
	 * 
	 * @param name
	 *            sensor name
	 * @param listener
	 *            sensor listener
	 * @param gpioController
	 *            controller
	 * @param pin
	 *            pin which this sensor is associated with
	 */
	public TactileSwitchResource(String name, DeviceResourceListener listener, GpioController gpioController, Pin pin) {
		super(name, listener, SensorType.TACTILE_SWITCH, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalInput getInput() {
		GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, name, PinPullResistance.PULL_DOWN);
		input.setShutdownOptions(true);
		return input;
	}
}
