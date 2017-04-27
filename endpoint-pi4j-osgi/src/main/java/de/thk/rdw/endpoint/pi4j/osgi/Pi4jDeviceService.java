package de.thk.rdw.endpoint.pi4j.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.pi4j.osgi.resources.ActuatorPi4jResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.LedResource;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private List<DeviceListener> deviceListeners = new ArrayList<>();

	private NavigableMap<Integer, ActuatorPi4jResource> actuators = new TreeMap<>();

	private GpioController gpioController;

	private LedResource ledResource;

	private GpioPinDigitalInput tilt;
	private GpioPinDigitalInput push;
	private GpioPinDigitalInput motion;

	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();

		ledResource = new LedResource("led", ActuatorType.LED, gpioController, RaspiPin.GPIO_00);

		tilt = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01, "tilt");
		tilt.setShutdownOptions(true);
		tilt.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				notifyListeners(0, event.getState().getName());
			}
		});
		push = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_02, "push", PinPullResistance.PULL_DOWN);
		push.setShutdownOptions(true);
		push.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				notifyListeners(1, event.getState().getName());
			}
		});
		motion = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_03, "motion", PinPullResistance.PULL_UP);
		motion.setShutdownOptions(true);
		motion.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				notifyListeners(2, event.getState().getName());
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
	public void toggleActuator(int id) {
		// GpioPin pin = gpioController.getProvisionedPin(name);
		// if (pin != null) {
		// ((GpioPinDigitalOutput) pin).toggle();
		// LOGGER.log(Level.INFO, "Toggled actuator with name \"{0}\".", new
		// Object[] { name });
		// } else {
		// LOGGER.log(Level.WARNING, "Actuator with name \"{0}\" does not
		// exists.", new Object[] { name });
		// }
	}

	public void deactivate() {
		ledResource.destroy();

		// Calling GpioController.shutdown() causes
		// java.util.concurrent.RejectedExecutionException in
		// listeners after bundle is started again.

		gpioController.unprovisionPin(tilt);
		gpioController.unprovisionPin(push);
		gpioController.unprovisionPin(motion);
		LOGGER.log(Level.INFO, "All GPIO pins have been unprovisioned.");
	}

	private void notifyListeners(Integer id, Object newValue) {
		for (DeviceListener deviceListener : deviceListeners) {
			deviceListener.onSensorChanged(id, newValue);
		}
	}

	@Override
	public Map<Integer, Entry<SensorType, String>> getSensors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Entry<ActuatorType, String>> getActuators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSensorValue(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getActuatorValue(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActuatorValue(int id, Object value) {
		// TODO Auto-generated method stub

	}
}
