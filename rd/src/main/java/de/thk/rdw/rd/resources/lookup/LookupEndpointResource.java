package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.thk.rdw.rd.resources.RdResource;

public class LookupEndpointResource extends CoapResource {

	private RdResource rdResource = null;

	public LookupEndpointResource(RdResource rdResource) {
		super(LookupType.ENDPOINT.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// TODO Auto-generated method stub
		super.handleGET(exchange);
	}
}
