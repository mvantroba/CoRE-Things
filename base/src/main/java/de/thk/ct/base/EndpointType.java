package de.thk.ct.base;

public enum EndpointType {

	RASPBERRY_PI("raspberrypi"), //
	OPENHAB("openhab");

	private String type;

	private EndpointType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public EndpointType get(String type) {
		EndpointType result = null;
		for (EndpointType endpointType : values()) {
			if (type.equals(endpointType.getType())) {
				result = endpointType;
				break;
			}
		}
		return result;
	}

	public String getType() {
		return type;
	}
}
