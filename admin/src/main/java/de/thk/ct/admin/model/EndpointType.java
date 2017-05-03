package de.thk.ct.admin.model;

public enum EndpointType {

	RASPBERRY_PI("raspberrypi"), OPENHAB("openhab");

	private String name;

	private EndpointType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public EndpointType getByName(String name) {
		EndpointType result = null;
		for (EndpointType resourceType : values()) {
			if (name.equals(resourceType.getName())) {
				result = resourceType;
				break;
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}
}
