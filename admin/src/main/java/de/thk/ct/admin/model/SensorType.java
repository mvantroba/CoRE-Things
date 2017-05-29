package de.thk.ct.admin.model;

import java.util.List;

/**
 * Definitions of all supported sensors.
 * 
 * @author Martin Vantroba
 *
 */
public enum SensorType {

	MERCURY_SWITCH("mercurySwitch"), //
	TACTILE_SWITCH("tactileSwitch"), //
	PIR_SENSOR("pirSensor"), //
	DS18B20("ds18b20");

	private String type;

	private SensorType(String type) {
		this.type = String.format("%s.%s", ResourceType.SENSOR.getType(), type);
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * Searches a defined sensor type by the given type.
	 * 
	 * @param type
	 *            sensor type
	 * @return defined sensor type if found
	 */
	public static SensorType get(String type) {
		SensorType result = null;
		for (SensorType sensorType : values()) {
			if (type.equals(sensorType.getType())) {
				result = sensorType;
				break;
			}
		}
		return result;
	}

	/**
	 * Checks if the given sensor type is defined.
	 * 
	 * @param type
	 *            sensor type
	 * @return true if found
	 */
	public static boolean containsType(String type) {
		return get(type) != null;
	}

	/**
	 * Checks if one of the given sensor types is defined.
	 * 
	 * @param types
	 *            list of sensor types
	 * @return true if one of the given types was found
	 */
	public static boolean containsTypes(List<String> types) {
		boolean result = false;
		for (String type : types) {
			if (containsType(type)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public String getType() {
		return type;
	}
}
