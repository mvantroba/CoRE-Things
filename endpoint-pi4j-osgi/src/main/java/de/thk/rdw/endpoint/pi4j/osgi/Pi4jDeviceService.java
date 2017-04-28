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
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.SensorType;
import de.thk.rdw.endpoint.device.osgi.DeviceListener;
import de.thk.rdw.endpoint.device.osgi.DeviceService;
import de.thk.rdw.endpoint.pi4j.osgi.resources.ActuatorPi4jResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.LedResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.MercurySwitchResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.PirSensorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.TactileSwitchResource;

public class Pi4jDeviceService implements DeviceService {

	private static final Logger LOGGER = Logger.getLogger(Pi4jDeviceService.class.getName());

	private List<DeviceListener> deviceListeners = new ArrayList<>();

	private NavigableMap<Integer, ActuatorPi4jResource> actuators = new TreeMap<>();

	private GpioController gpioController;

	private LedResource ledResource;
	private MercurySwitchResource mercurySwitchResource;
	private TactileSwitchResource tactileSwitchResource;
	private PirSensorResource pirSensorResource;

	public Pi4jDeviceService() {
		gpioController = GpioFactory.getInstance();

		ledResource = new LedResource("led", ActuatorType.LED, gpioController, RaspiPin.GPIO_00);

		mercurySwitchResource = new MercurySwitchResource("tilt", SensorType.MERCURY_SWITCH, gpioController,
				RaspiPin.GPIO_01);
		mercurySwitchResource.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				notifyListeners(0, event.getState().getName());
			}
		});

		tactileSwitchResource = new TactileSwitchResource("tactileSwitch", SensorType.TACTILE_SWITCH, gpioController,
				RaspiPin.GPIO_02);
		tactileSwitchResource.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				notifyListeners(1, event.getState().getName());
			}
		});

		pirSensorResource = new PirSensorResource("motion", SensorType.PIR_SENSOR, gpioController, RaspiPin.GPIO_03);
		pirSensorResource.addListener(new GpioPinListenerDigital() {

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
		mercurySwitchResource.destroy();
		tactileSwitchResource.destroy();
		pirSensorResource.destroy();

		// Calling GpioController.shutdown() causes
		// java.util.concurrent.RejectedExecutionException in
		// listeners after bundle is started again.

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
