package de.thk.ct.base;

import java.util.List;

public enum SensorType {

	MERCURY_SWITCH("mercurySwitch"), //
	TACTILE_SWITCH("tactileSwitch"), //
	PIR_SENSOR("pirSensor");

	private String type;

	private SensorType(String type) {
		this.type = String.format("%s.%s", ResourceType.SENSOR.getType(), type);
	}

	@Override
	public String toString() {
		return type;
	}

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

	public static boolean containsType(String type) {
		return get(type) != null;
	}

	public static boolean containsTypes(List<String> resourceTypes) {
		boolean result = false;
		for (String type : resourceTypes) {
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
