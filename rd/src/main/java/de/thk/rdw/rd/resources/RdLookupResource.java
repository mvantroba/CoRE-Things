package de.thk.rdw.rd.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;

import de.thk.rdw.base.RdResourceType;
import de.thk.rdw.rd.resources.lookup.LookupDomainResource;
import de.thk.rdw.rd.resources.lookup.LookupEndpointResource;
import de.thk.rdw.rd.resources.lookup.LookupGroupResource;
import de.thk.rdw.rd.resources.lookup.LookupResourceResource;

/**
 * The {@link CoapServer} type which implements the CoRE Resource Directory
 * lookup interfaces. It serves as a top level resource which initializes
 * specialized lookup resources.
 * <p>
 * The specialized lookup resources are {@link LookupDomainResource},
 * {@link LookupEndpointResource}, {@link LookupResourceResource} and
 * {@link LookupGroupResource}.
 * 
 * @author Martin Vantroba
 *
 */
public class RdLookupResource extends CoapResource {

	/**
	 * Constructs a {@link RdLookupResource} and initializes all specialized
	 * lookup resources.
	 * 
	 * @param rdResource
	 *            resource directory resource
	 * @param rdGroupResource
	 *            resource directory group resource
	 */
	public RdLookupResource(RdResource rdResource, RdGroupResource rdGroupResource) {
		super(RdResourceType.CORE_RD_LOOKUP.getName());
		getAttributes().addResourceType(RdResourceType.CORE_RD_LOOKUP.getType());
		add(new LookupDomainResource(rdResource));
		add(new LookupEndpointResource(rdResource));
		add(new LookupResourceResource(rdResource));
		add(new LookupGroupResource(rdGroupResource));
	}
}
