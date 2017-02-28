package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;

public class GroupResource extends CoapResource {

	public GroupResource() {
		super("rd-group");
		getAttributes().addResourceType("core.rd-group");
	}
}
