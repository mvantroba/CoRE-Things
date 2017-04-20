package de.thk.rdw.rd.uri;

public enum UriVariableDefault {

	DOMAIN("local"), //
	LIFE_TIME(86400L); // 24 hours

	private Object defaultValue;

	private UriVariableDefault(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return String.valueOf(defaultValue);
	}
}
