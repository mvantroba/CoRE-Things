package de.thk.ct.admin.model;

/**
 * Definitions used to categorize sensors and actuators.
 * 
 * @author Martin Vantroba
 *
 */
public enum ResourceType {

	SENSOR("s", "sen"), //
	ACTUATOR("a", "act");

	public static final String prefix = "corethings";

	private String name;
	private String type;

	private ResourceType(String name, String type) {
		this.name = name;
		this.type = String.format("%s.%s", prefix, type);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
