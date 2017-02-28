package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;

public class LookupResource extends CoapResource {

	public LookupResource() {
		super("rd-lookup");
		getAttributes().addResourceType("core.rd-lookup");
	}
}
