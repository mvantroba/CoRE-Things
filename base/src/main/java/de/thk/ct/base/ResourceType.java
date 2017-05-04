package de.thk.ct.base;

public enum ResourceType {

	SENSOR("s", "sen"), //
	ACTUATOR("a", "act");

	public static final String prefix = "corethings";

	private String name;
	private String type;

	private ResourceType(String name, String type) {
		this.name = name;
		this.type = prefix + "." + type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
