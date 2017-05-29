package de.thk.ct.admin.model;

/**
 * Definitions for all supported endpoint types.
 * 
 * @author Martin Vantroba
 *
 */
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

	/**
	 * Searches a defined endpoint type by the given type.
	 * 
	 * @param type
	 *            endpoint type
	 * @return defined endpoint type if found
	 */
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
