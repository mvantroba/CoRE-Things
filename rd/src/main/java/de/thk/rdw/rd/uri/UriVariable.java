package de.thk.rdw.rd.uri;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.LinkFormat;

/**
 * Definitions of all URI variables that registration and lookup interfaces of
 * this Resource Directory use. Every variable is defined by its name and
 * implements its own {@link UriVariableValidator} which is used by
 * {@link UriVariable #validate(String)} method.
 * 
 * @author Martin Vantroba
 *
 */
public enum UriVariable {

	/**
	 * Endpoint name
	 */
	END_POINT(LinkFormat.END_POINT, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String.format(
						"Endpoint name too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}),

	/**
	 * Domain
	 */
	DOMAIN(LinkFormat.DOMAIN, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String
						.format("Domain name too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}),

	/**
	 * Endpoint type
	 */
	END_POINT_TYPE(LinkFormat.END_POINT_TYPE, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String.format(
						"Endpoint type too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}),

	/**
	 * Lifetime
	 */
	LIFE_TIME(LinkFormat.LIFE_TIME, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			long minValue = 60L;
			long maxValue = 4294967295L;
			try {
				Long valueNumeric = Long.parseLong(value);
				if (valueNumeric < minValue || valueNumeric > maxValue) {
					throw new IllegalArgumentException(String.format(
							"Lifetime must be in range of %d - %d. Received: %d.", minValue, maxValue, valueNumeric));
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Lifetime must be an integer. Received: %s.", value));
			}
		}
	}),

	/**
	 * Context (scheme, host and port)
	 */
	CONTEXT(LinkFormat.CONTEXT, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			// Manual validation combined with request object is required.
		}
	}),

	/**
	 * Group name
	 */
	GROUP("gp", new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String
						.format("Group name too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}),

	/**
	 * Page used to define page of resources
	 */
	PAGE("page", new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int minValue = 0;
			try {
				int valueNumeric = Integer.parseInt(value);
				if (valueNumeric < minValue) {
					throw new IllegalArgumentException(
							String.format("Page must be larger than %d. Received: %d.", minValue, valueNumeric));
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Page must be an integer. Received: %s.", value));
			}
		}
	}),

	/**
	 * Count used to define number of resources
	 */
	COUNT("count", new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int minValue = 0;
			try {
				int valueNumeric = Integer.parseInt(value);
				if (valueNumeric < minValue) {
					throw new IllegalArgumentException(
							String.format("Count must be larger than %d. Received: %d.", minValue, valueNumeric));
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Count must be an integer. Received: %s.", value));
			}
		}
	});

	private String name;
	private UriVariableValidator validator;

	private UriVariable(String name, UriVariableValidator validator) {
		this.name = name;
		this.validator = validator;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Searches an {@link UriVariable} by the given name.
	 * 
	 * @param name
	 *            URI variable name
	 * @return URI variable, null if not found
	 */
	public static UriVariable getByName(String name) {
		UriVariable result = null;
		for (UriVariable variable : UriVariable.values()) {
			if (variable.getName().equalsIgnoreCase(name)) {
				result = variable;
			}
		}
		return result;
	}

	/**
	 * Calls the validate method of this variable's {@link UriVariableValidator}
	 * implementation with the given value.
	 * 
	 * @param value
	 *            value which will be evaluated
	 */
	public void validate(String value) {
		validator.validate(value);
	}

	public String getName() {
		return name;
	}
}
