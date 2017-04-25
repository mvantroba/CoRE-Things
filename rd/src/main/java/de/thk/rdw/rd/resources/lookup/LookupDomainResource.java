package de.thk.rdw.rd.resources.lookup;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.base.RdLookupType;
import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdResource;

/**
 * The {@link CoapResource} type which allows to perform a domain lookup. List
 * of domains can be retrieved by sending a GET request to this resource. The
 * {@link #handleGET(CoapExchange)} method iterates over all endpoints
 * registered on the {@link RdResource} and reads their domain names.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupDomainResource extends CoapResource {

	private RdResource rdResource;

	/**
	 * Creates a {@link LookupDomainResource} with name according to the
	 * {@link LinkFormat} and initializes the {@link RdResource} which is needed
	 * to retrieve list of domains.
	 * 
	 * @param rdResource
	 *            Resource to which endpoints register themselves.
	 */
	public LookupDomainResource(RdResource rdResource) {
		super(RdLookupType.DOMAIN.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String resultPayload = "";
		// Use TreeSet to prevent duplicates.
		Set<String> domainNames = new TreeSet<>();

		// Search for existing domains by iterating over all endpoints
		// registered on the RD resource.
		for (Resource resource : rdResource.getChildren()) {
			if (resource instanceof EndpointResource) {
				domainNames.add(((EndpointResource) resource).getDomain());
			}
		}

		// Create Resource object for every domain for easier serialization.
		for (String domainName : domainNames) {
			resultPayload += LinkFormat.serializeResource(new CoapResource(domainName));
		}

		if (!resultPayload.isEmpty()) {
			exchange.respond(ResponseCode.CONTENT, resultPayload, MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
