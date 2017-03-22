package de.thk.rdw.endpoint.server.osgi;

public enum ResourceProfile {

	ACTUATORS("a", "act"), //
	SENSORS("s", "sen");

	private String name;
	private String resourceType;

	private ResourceProfile(String name, String resourceType) {
		this.name = name;
		this.resourceType = resourceType;
	}

	public String getName() {
		return name;
	}

	public String getResourceType() {
		return resourceType;
	}
}
