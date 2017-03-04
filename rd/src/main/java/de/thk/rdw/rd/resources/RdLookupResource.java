package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;

public class RdLookupResource extends CoapResource {

	public RdLookupResource() {
		super("rd-lookup");
		getAttributes().addResourceType("core.rd-lookup");
	}
}
