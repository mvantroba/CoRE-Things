package de.thk.ct.admin.icon;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.thk.ct.admin.model.EndpointType;

public class EndpointTypeIcon {

	private static final Icon DEFAULT_ICON = Icon.ENDPOINT_GREY;
	private static final Map<EndpointType, Icon> ENDPOINT_TYPE_ICONS = new EnumMap<>(EndpointType.class);

	static {
		ENDPOINT_TYPE_ICONS.put(EndpointType.RASPBERRY_PI, Icon.RASPBERRY);
		ENDPOINT_TYPE_ICONS.put(EndpointType.OPENHAB, Icon.OPENHAB);
	}

	public static Icon get(EndpointType endpointType) {
		Icon result;
		if (ENDPOINT_TYPE_ICONS.containsKey(endpointType)) {
			result = ENDPOINT_TYPE_ICONS.get(endpointType);
		} else {
			result = DEFAULT_ICON;
		}
		return result;
	}

	public static Icon get(List<String> endpointTypes) {
		Icon result;
		if (endpointTypes == null) {
			result = DEFAULT_ICON;
		} else {
			result = findFirst(endpointTypes);
			if (result == null) {
				result = DEFAULT_ICON;
			}
		}
		return result;
	}

	private static Icon findFirst(List<String> endpointTypes) {
		Icon result = null;
		boolean found = false;
		for (String endpointType : endpointTypes) {
			for (Entry<EndpointType, Icon> entry : ENDPOINT_TYPE_ICONS.entrySet()) {
				if (endpointType.equals(entry.getKey().getType())) {
					result = get(entry.getKey());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		return result;
	}
}
