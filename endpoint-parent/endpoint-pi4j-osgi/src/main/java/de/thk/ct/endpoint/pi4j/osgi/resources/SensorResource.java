package de.thk.ct.endpoint.pi4j.osgi.resources;

import de.thk.ct.endpoint.device.osgi.resources.SensorType;

/**
 * Superclass for all sensors in this application. Every sensor has to be type
 * of the {@link SensorType}.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class SensorResource extends AbstractDeviceResource {

	protected SensorType sensorType;

	/**
	 * Constructs the {@link SensorResource} with the given name, listener and
	 * type.
	 * 
	 * @param name
	 *            sensor name
	 * @param listener
	 *            sensor listener
	 * @param sensorType
	 *            sensor type
	 */
	public SensorResource(String name, DeviceResourceListener listener, SensorType sensorType) {
		super(name, listener);
		this.sensorType = sensorType;
	}

	@Override
	public String getType() {
		return sensorType.getType();
	}

	public SensorType getSensorType() {
		return sensorType;
	}
}
