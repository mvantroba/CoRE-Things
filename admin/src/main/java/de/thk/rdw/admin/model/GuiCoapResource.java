package de.thk.rdw.admin.model;

import java.util.List;

import org.eclipse.californium.core.CoapResource;

public class GuiCoapResource extends CoapResource {

	public GuiCoapResource(String name) {
		super(name);
	}

	public String getAttributeValues(String attr) {
		StringBuilder result = new StringBuilder();
		List<String> values = getAttributes().getAttributeValues(attr);
		String separator = ", ";
		if (values == null || values.isEmpty()) {
			// TODO Localize this String.
			result.append("undefined");
		} else {
			for (int i = 0; i < values.size(); i++) {
				result.append(values.get(i));
				if (i < values.size() - 1) {
					result.append(separator);
				}
			}
		}
		return result.toString();
	}
}
