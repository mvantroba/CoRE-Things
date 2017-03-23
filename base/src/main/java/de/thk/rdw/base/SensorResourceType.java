package de.thk.rdw.base;

import java.util.List;

public enum SensorResourceType {

	TILT("tilt", "tilt");

	private String name;
	private String resourceType;

	private SensorResourceType(String name, String resourceType) {
		this.name = name;
		this.resourceType = resourceType;
	}

	public static boolean containsTypes(List<String> resourceTypes) {
		boolean result = false;
		for (String type : resourceTypes) {
			for (SensorResourceType actuatorResourceType : values()) {
				if (type.equals(actuatorResourceType.getResourceType())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public String getResourceType() {
		return resourceType;
	}
}
