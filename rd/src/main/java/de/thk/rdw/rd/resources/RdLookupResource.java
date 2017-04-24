package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;

import de.thk.rdw.rd.resources.lookup.LookupDomainResource;
import de.thk.rdw.rd.resources.lookup.LookupEndpointResource;
import de.thk.rdw.rd.resources.lookup.LookupGroupResource;
import de.thk.rdw.rd.resources.lookup.LookupResourceResource;

public class RdLookupResource extends CoapResource {

	public RdLookupResource(RdResource rdResource, RdGroupResource rdGroupResource) {
		super(ResourceType.CORE_RD_LOOKUP.getName());
		getAttributes().addResourceType(ResourceType.CORE_RD_LOOKUP.getType());
		add(new LookupDomainResource(rdResource));
		add(new LookupEndpointResource(rdResource, rdGroupResource));
		add(new LookupResourceResource(rdResource));
		add(new LookupGroupResource(rdGroupResource));
	}
}
