package de.thk.rdw.admin.model;

public enum ResourceType {

	CORE_RD("core.rd"), //
	CORE_RD_GROUP("core.rd-group"), //
	CORE_RD_LOOKUP("core.rd-lookup"), //
	LED("led");

	private String name;

	private ResourceType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public ResourceType getByName(String name) {
		ResourceType result = null;
		for (ResourceType resourceType : values()) {
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
