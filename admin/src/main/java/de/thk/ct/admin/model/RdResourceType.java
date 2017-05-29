package de.thk.ct.admin.model;

/**
 * Definitions of main resources available in the resource directory.
 * 
 * @author Martin Vantroba
 *
 */
public enum RdResourceType {

	CORE_RD("rd", "core.rd"), //
	CORE_RD_GROUP("rd-group", "core.rd-group"), //
	CORE_RD_LOOKUP("rd-lookup", "core.rd-lookup");

	private String name;
	private String type;

	private RdResourceType(String name, String type) {
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
