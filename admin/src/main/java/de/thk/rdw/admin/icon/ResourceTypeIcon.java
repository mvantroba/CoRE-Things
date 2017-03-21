package de.thk.rdw.admin.icon;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.thk.rdw.admin.model.ResourceType;

public class ResourceTypeIcon {

	private static final Icon DEFAULT_ICON = Icon.RESOURCE_GREY;
	private static final Map<ResourceType, Icon> RESOURCE_TYPE_ICONS = new EnumMap<>(ResourceType.class);

	static {
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD, Icon.FOLDER_AMBER);
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD_GROUP, Icon.GROUP_WORK_GREY);
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD_LOOKUP, Icon.PAGEVIEW_GREY);
		RESOURCE_TYPE_ICONS.put(ResourceType.LED, Icon.LED_LIGHT_LAMP);
	}

	public static Icon get(ResourceType resourceType) {
		Icon result;
		if (RESOURCE_TYPE_ICONS.containsKey(resourceType)) {
			result = RESOURCE_TYPE_ICONS.get(resourceType);
		} else {
			result = DEFAULT_ICON;
		}
		return result;
	}

	public static Icon get(List<String> resourceTypes) {
		Icon result;
		if (resourceTypes == null) {
			result = DEFAULT_ICON;
		} else {
			result = findFirst(resourceTypes);
			if (result == null) {
				result = DEFAULT_ICON;
			}
		}
		return result;
	}

	private static Icon findFirst(List<String> resourceTypes) {
		Icon result = null;
		boolean found = false;
		for (String resourceType : resourceTypes) {
			for (Entry<ResourceType, Icon> entry : RESOURCE_TYPE_ICONS.entrySet()) {
				if (resourceType.equals(entry.getKey().getName())) {
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
