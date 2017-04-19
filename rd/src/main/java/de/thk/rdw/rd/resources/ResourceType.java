package de.thk.rdw.rd.resources;

public enum ResourceType {

	CORE_RD("rd", "core.rd"), //
	CORE_RD_GROUP("rd-group", "core.rd-group"), //
	CORE_RD_LOOKUP("rd-lookup", "core.rd-lookup");

	private String name;
	private String type;

	private ResourceType(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
