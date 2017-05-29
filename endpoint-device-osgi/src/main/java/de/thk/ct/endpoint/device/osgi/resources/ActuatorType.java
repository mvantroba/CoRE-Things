package de.thk.ct.endpoint.device.osgi.resources;

import java.util.List;

/**
 * Definitions of all supported actuators.
 * 
 * @author Martin Vantroba
 *
 */
public enum ActuatorType {

	LED("led"), //
	LCD16X2("lcd16x2");

	private String type;

	private ActuatorType(String type) {
		this.type = String.format("%s.%s", ResourceType.ACTUATOR.getType(), type);
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * Searches a defined actuator type by the given type.
	 * 
	 * @param type
	 *            actuator type
	 * @return defined actuator type if found
	 */
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

	/**
	 * Checks if the given actuator type is defined.
	 * 
	 * @param type
	 *            actuator type
	 * @return true if found
	 */
	public static boolean containsType(String type) {
		return get(type) != null;
	}

	/**
	 * Checks if one of the given actuator types is defined.
	 * 
	 * @param types
	 *            list of actuator types
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
