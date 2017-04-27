package de.thk.rdw.base;

import java.util.List;

public enum SensorType {

	TILT("tilt"), //
	PUSH("push"), //
	MOTION("motion");

	private String type;

	private SensorType(String type) {
		this.type = type;
	}

	public static boolean containsTypes(List<String> resourceTypes) {
		boolean result = false;
		for (String type : resourceTypes) {
			for (SensorType actuatorResourceType : values()) {
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
