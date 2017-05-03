package de.thk.ct.rd.uri;

/**
 * Defines default values for some of the URI variables which are defined in
 * {@link UriVariable}.
 * 
 * @author Martin Vantroba
 *
 */
public enum UriVariableDefault {

	/**
	 * Domain
	 */
	DOMAIN("local"),

	/**
	 * Lifetime
	 */
	LIFE_TIME(86400L); // 24 hours

	private Object defaultValue;

	private UriVariableDefault(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return String.valueOf(defaultValue);
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
