package de.thk.rdw.rd.resources.lookup;

import java.util.ArrayList;
import java.util.List;
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
 * The {@link AbstractLookupResource} type which allows to perform a domain
 * lookup. List of domains can be retrieved by sending a GET request to this
 * resource. The {@link #handleGET(CoapExchange)} method iterates over all
 * endpoints registered on the {@link RdResource} and reads their domain names.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupDomainResource extends AbstractLookupResource {

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
		String resultPayload;
		Map<UriVariable, String> uriVariables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		// Use TreeSet to prevent duplicates.
		Set<String> domainNames = new TreeSet<>();
		List<CoapResource> domains = new ArrayList<>();

		// Search for existing domains by iterating over all endpoints
		// registered on the RD resource.
		for (Resource resource : rdResource.getChildren()) {
			if (resource instanceof EndpointResource) {
				domainNames.add(((EndpointResource) resource).getDomain());
			}
		}

		// Create CoapResource object for every domain for easier serialization.
		for (String domainName : domainNames) {
			domains.add(new CoapResource(domainName));
		}

		resultPayload = toLinkFormat(domains, uriVariables.get(UriVariable.PAGE), uriVariables.get(UriVariable.COUNT));

		if (!resultPayload.isEmpty()) {
			exchange.respond(ResponseCode.CONTENT, resultPayload, MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
