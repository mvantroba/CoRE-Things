package de.thk.rdw.base;

public enum ActuatorResourceType {

	LED("led", "led");

	private String name;
	private String resourceType;

	private ActuatorResourceType(String name, String resourceType) {
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
