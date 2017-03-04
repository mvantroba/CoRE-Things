package de.thk.rdw.rd.uri;

public enum UriVariableDefault {

	DOMAIN("local"), LIFE_TIME(86400L);

	private Object defaultValue;

	private UriVariableDefault(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
