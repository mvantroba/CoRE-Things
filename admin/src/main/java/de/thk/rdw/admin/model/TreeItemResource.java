package de.thk.rdw.admin.model;

import org.eclipse.californium.core.CoapResource;

public class TreeItemResource extends CoapResource {

	public TreeItemResource(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return getName();
	}
}
