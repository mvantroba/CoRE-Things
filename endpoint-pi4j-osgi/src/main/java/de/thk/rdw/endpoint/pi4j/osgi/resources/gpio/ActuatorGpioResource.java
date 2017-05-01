package de.thk.rdw.endpoint.pi4j.osgi.resources.gpio;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.endpoint.pi4j.osgi.resources.ActuatorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.DeviceResourceListener;

/**
 * The type of {@link ActuatorResource} which uses GPIO pins and
 * {@link GpioController}.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class ActuatorGpioResource extends ActuatorResource {

	private static final Logger LOGGER = Logger.getLogger(ActuatorGpioResource.class.getName());

	protected GpioController gpioController;
	protected Pin pin;

	private GpioPinDigitalOutput output;
	private GpioPinListener gpioPinListener;

	/**
	 * Constructs a {@link ActuatorGpioResource} which will be managed by the
	 * given {@link GpioController}.
	 * 
	 * @param name
	 *            actuator name
	 * @param listener
	 *            actuator listener
	 * @param actuatorType
	 *            actuator type
	 * @param gpioController
	 *            controller
	 * @param pin
	 *            pin which this actuator is associated with
	 */
	public ActuatorGpioResource(String name, DeviceResourceListener listener, ActuatorType actuatorType,
			GpioController gpioController, Pin pin) {
		super(name, listener, actuatorType);
		this.gpioController = gpioController;
		this.pin = pin;
		this.gpioPinListener = new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				onChanged(event.getState().getName());
			}
		};
	}

	/**
	 * Returns output pin interface, that will be used to control state of this
	 * resource.
	 * 
	 * @return output pin interface
	 */
	protected abstract GpioPinDigitalOutput getOutput();

	@Override
	public String toString() {
		return "Actuator [pin=" + pin + ", name=" + name + ", actuatorType=" + actuatorType + "]";
	}

	@Override
	public void init() {
		output = getOutput();
		if (gpioPinListener != null) {
			output.addListener(gpioPinListener);
		} else {
			LOGGER.log(Level.WARNING, "Could not add pin listener to {0}, because it is not initialized.",
					new Object[] { toString() });
		}
		LOGGER.log(Level.INFO, "Initialized {0}.", new Object[] { toString() });
	}

	@Override
	public void destroy() {
		output.setState(PinState.LOW);
		output.removeAllListeners();
		gpioController.unprovisionPin(output);
		LOGGER.log(Level.INFO, "Destroyed {0}.", new Object[] { toString() });
	}

	@Override
	public String getValue() {
		String result = null;
		if (output != null) {
			result = output.getState().toString();
		} else {
			LOGGER.log(Level.WARNING, "Could not read value of {0}, because pin is not provisioned.",
					new Object[] { toString() });
		}
		return result;
	}

	@Override
	public void toggle() {
		if (output != null) {
			output.toggle();
		} else {
			LOGGER.log(Level.WARNING, "Could not toggle {0}, because pin is not provisioned.",
					new Object[] { toString() });
		}
	}

	@Override
	public void setValue(String value) {
		if (output != null) {
			try {
				int state = Integer.parseInt(value);
				if (state == 0) {
					output.setState(PinState.LOW);
				} else if (state == 1) {
					output.setState(PinState.HIGH);
				} else {
					LOGGER.log(Level.WARNING, "Invalid value {0} for {1}.", new Object[] { value, toString() });
				}
			} catch (NumberFormatException e) {
				if (value.equalsIgnoreCase(PinState.LOW.getName())) {
					output.setState(PinState.LOW);
				} else if (value.equalsIgnoreCase(PinState.HIGH.getName())) {
					output.setState(PinState.HIGH);
				} else {
					LOGGER.log(Level.WARNING, "Invalid value {0} for {1}.", new Object[] { value, toString() });
				}
			}
		} else {
			LOGGER.log(Level.WARNING, "Could not update value for {0}, because pin is not provisioned.",
					new Object[] { toString() });
		}
	}
}
