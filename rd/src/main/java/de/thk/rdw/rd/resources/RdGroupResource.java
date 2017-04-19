package de.thk.rdw.rd.resources;

import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

public class RdGroupResource extends CoapResource {

	public RdGroupResource() {
		super(ResourceType.CORE_RD_GROUP.getName());
		getAttributes().addResourceType(ResourceType.CORE_RD_GROUP.getType());
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		if (variables.containsKey(UriVariable.GROUP)) {
			GroupResource groupResource = new GroupResource(variables.get(UriVariable.GROUP),
					variables.get(UriVariable.DOMAIN));
			GroupResource existingGroup = findChildGroupResource(groupResource);
			if (existingGroup == null) {
				add(groupResource);
			}
		}
		exchange.respond(ResponseCode.CREATED);
	}

	private GroupResource findChildGroupResource(GroupResource groupResource) {
		GroupResource result = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((GroupResource) child).getDomain();
			if (childName != null && childName.equals(groupResource.getName()) && childDomain != null
					&& childDomain.equals(groupResource.getDomain())) {
				result = (GroupResource) child;
				break;
			}
		}
		return result;
	}
}
