package de.thk.ct.rd.resources.lookup;

import org.eclipse.californium.core.coap.LinkFormat;

/**
 * Definitions of lookup resources supported by the resource directory.
 * 
 * @author Martin Vantroba
 *
 */
public enum RdLookupType {

	DOMAIN(LinkFormat.DOMAIN), //
	ENDPOINT(LinkFormat.END_POINT), //
	RESOURCE("res"), //
	GROUP("gp");

	private String type;

	private RdLookupType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public String getType() {
		return type;
	}
}
