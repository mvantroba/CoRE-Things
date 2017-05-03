package de.thk.ct.rd.uri;

/**
 * Validation interface which variables defined in {@link UriVariable}
 * implement.
 * 
 * @author Martin Vantroba
 *
 */
public interface UriVariableValidator {

	/**
	 * Validates the given value.
	 * 
	 * @param value
	 *            value of the URI variable
	 */
	void validate(String value);
}
