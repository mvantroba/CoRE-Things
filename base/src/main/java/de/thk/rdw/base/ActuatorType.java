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

	public static boolean containsTypes(List<String> resourceTypes) {
		boolean result = false;
		for (String type : resourceTypes) {
			for (ActuatorType actuatorResourceType : values()) {
				if (type.equals(actuatorResourceType.getType())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public String getType() {
		return type;
	}
}
