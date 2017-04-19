package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.coap.LinkFormat;

/**
 * https://tools.ietf.org/html/draft-ietf-core-resource-directory-08#section-8
 * 
 * @author Martin Vantroba
 *
 */
public enum LookupType {

	DOMAIN(LinkFormat.DOMAIN), //
	ENDPOINT(LinkFormat.END_POINT), //
	RESOURCE("res"), //
	GROUP("gp");

	private String type;

	private LookupType(String type) {
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
