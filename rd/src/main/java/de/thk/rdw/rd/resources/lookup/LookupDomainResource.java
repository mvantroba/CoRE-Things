package de.thk.rdw.rd.resources.lookup;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdResource;
import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

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
		super(LookupType.DOMAIN.getType());
		this.rdResource = rdResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		StringBuilder result = new StringBuilder();
		Map<UriVariable, String> uriVariables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		String domainVariable = uriVariables.get(UriVariable.DOMAIN);
		Collection<Resource> rdChildren = rdResource.getChildren();
		// Use TreeSet to prevent duplicates.
		Set<String> domains = new TreeSet<>();

		// Search for existing domains by iterating over all endpoints
		// registered on the RD resource.
		for (Resource rdChild : rdChildren) {
			if (rdChild instanceof EndpointResource) {
				EndpointResource endpointResource = (EndpointResource) rdChild;
				if (domainVariable == null || domainVariable.equals(endpointResource.getDomain())) {
					domains.add(endpointResource.getDomain());
				}
			}
		}

		if (!domains.isEmpty()) {
			for (String domain : domains) {
				// Format response according to
				// https://tools.ietf.org/html/draft-ietf-core-resource-directory-08#section-8
				result.append(String.format("<>;%s=\"%s\",", LinkFormat.DOMAIN, domain));
			}
			// Remove trailing comma from payload.
			exchange.respond(ResponseCode.CONTENT, result.substring(0, result.length() - 1),
					MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			// Respond with an error code when no domains were found.
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
