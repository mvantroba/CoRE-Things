package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.rdw.rd.resources.RdGroupResource;

public class LookupGroupResource extends CoapResource {

	private RdGroupResource rdGroupResource = null;

	public LookupGroupResource(RdGroupResource rdGroupResource) {
		super(LookupType.GROUP.getType());
		this.rdGroupResource = rdGroupResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// TODO Auto-generated method stub
		super.handleGET(exchange);
	}
}
