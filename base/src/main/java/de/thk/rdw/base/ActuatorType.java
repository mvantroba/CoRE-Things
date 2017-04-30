package de.thk.rdw.base;

import java.util.List;

public enum ActuatorType {

	LED("led");

	private String type;

	private ActuatorType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public static ActuatorType get(String type) {
		ActuatorType result = null;
		for (ActuatorType actuatorType : values()) {
			if (type.equals(actuatorType.getType())) {
				result = actuatorType;
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
