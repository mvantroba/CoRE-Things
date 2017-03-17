package de.thk.rdw.admin.model;

import org.eclipse.californium.core.CoapResource;

public class GuiCoapResource extends CoapResource {

	public GuiCoapResource(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return getName();
	}
}
