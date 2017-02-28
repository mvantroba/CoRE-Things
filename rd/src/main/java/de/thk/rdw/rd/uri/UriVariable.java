package de.thk.rdw.rd.uri;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.LinkFormat;

public enum UriVariable {

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
	}), DOMAIN(LinkFormat.DOMAIN, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String
						.format("Domain name too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}), END_POINT_TYPE(LinkFormat.END_POINT_TYPE, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			int maxLength = 63;
			int length = value.getBytes(CoAP.UTF8_CHARSET).length;
			if (length > maxLength) {
				throw new IllegalArgumentException(String.format(
						"Endpoint type too long. Max length: %d bytes. Received: %d bytes.", maxLength, length));
			}
		}
	}), LIFE_TIME(LinkFormat.LIFE_TIME, new UriVariableValidator() {

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
	}), CONTEXT(LinkFormat.CONTEXT, new UriVariableValidator() {

		@Override
		public void validate(String value) {
			try {
				new URI(value);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(String.format("Context has invalid syntax. Received: %s.", value));
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

	public static UriVariable getByName(String name) {
		UriVariable result = null;
		for (UriVariable variable : UriVariable.values()) {
			if (variable.getName().equals(name)) {
				result = variable;
			}
		}
		return result;
	}

	public void validate(String value) {
		validator.validate(value);
	}

	public String getName() {
		return name;
	}
}
