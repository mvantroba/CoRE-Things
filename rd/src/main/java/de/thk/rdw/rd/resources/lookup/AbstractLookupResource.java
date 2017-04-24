package de.thk.rdw.rd.resources.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.server.resources.Resource;

/**
 * The {@link CoapResource} type which implements basic functionality for lookup
 * resources. The goal of this class is to provide reusable methods for
 * filtering and pagination of resource lists which are returned by various
 * lookup resources.
 * 
 * @author Martin Vantroba
 *
 */
public abstract class AbstractLookupResource extends CoapResource {

	/**
	 * Constructs a new resource with the specified name.
	 *
	 * @param name
	 *            the name
	 */
	public AbstractLookupResource(String name) {
		super(name);
	}

	public String toLinkFormat(List<Resource> domains, String page, String count) {
		String result = "";
		StringBuilder builder = new StringBuilder();
		List<Resource> resourcesPage = extractPage(domains, page, count);

		if (!resourcesPage.isEmpty()) {
			for (Resource resource : resourcesPage) {
				builder.append(LinkFormat.serializeResource(resource));
			}
			// Remove trailing comma.
			result = builder.substring(0, builder.length() - 1);
		}
		return result;
	}

	private List<Resource> extractPage(List<Resource> domains, String page, String count) {
		List<Resource> result = new ArrayList<>();
		int pageNumeric = -1;
		int countNumeric = -1;
		try {
			pageNumeric = Integer.parseInt(page);
		} catch (NumberFormatException e) {
		}
		try {
			countNumeric = Integer.parseInt(count);
		} catch (NumberFormatException e) {
		}
		if (countNumeric >= 0) {
			if (pageNumeric >= 0) {
				int fromIndex = pageNumeric * countNumeric;
				try {
					result.addAll(domains.subList(fromIndex, fromIndex + countNumeric));
				} catch (IndexOutOfBoundsException e) {
					result.addAll(domains);
				}
			} else {
				result.addAll(domains.subList(0, countNumeric));
			}
		} else {
			result.addAll(domains);
		}
		return result;
	}
}
