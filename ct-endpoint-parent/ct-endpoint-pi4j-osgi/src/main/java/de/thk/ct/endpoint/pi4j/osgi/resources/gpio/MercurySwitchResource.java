package de.thk.ct.endpoint.pi4j.osgi.resources.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;

import de.thk.ct.endpoint.device.osgi.resources.SensorType;
import de.thk.ct.endpoint.pi4j.osgi.resources.DeviceResourceListener;

/**
 * The type of {@link SensorGpioResource} that represents mercury switch.
 * 
 * @author Martin Vantroba
 *
 */
public class MercurySwitchResource extends SensorGpioResource {

	/**
	 * Constructs a {@link MercurySwitchResource} which will be managed by the
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
	public MercurySwitchResource(String name, DeviceResourceListener listener, GpioController gpioController, Pin pin) {
		super(name, listener, SensorType.MERCURY_SWITCH, gpioController, pin);
	}

	@Override
	protected GpioPinDigitalInput getInput() {
		GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, name);
		input.setShutdownOptions(true);
		return input;
	}
}
