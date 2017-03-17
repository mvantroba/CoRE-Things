package de.thk.rdw.admin.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.thk.rdw.admin.controller.Icon;
import javafx.scene.image.ImageView;

public class ResourceTypeIcon {

	private static final Icon DEFAULT_ICON = Icon.RESOURCE_GREY_16;
	private static final Map<ResourceType, Icon> RESOURCE_TYPE_ICONS = new EnumMap<>(ResourceType.class);

	static {
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD, Icon.FOLDER_AMBER_16);
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD_GROUP, Icon.GROUP_WORK_GREY_16);
		RESOURCE_TYPE_ICONS.put(ResourceType.CORE_RD_LOOKUP, Icon.PAGEVIEW_GREY_16);
	}

	public static ImageView get(ResourceType resourceType) {
		ImageView result;
		if (RESOURCE_TYPE_ICONS.containsKey(resourceType)) {
			result = RESOURCE_TYPE_ICONS.get(resourceType).getImageView();
		} else {
			result = DEFAULT_ICON.getImageView();
		}
		return result;
	}

	public static ImageView get(List<String> resourceTypes) {
		ImageView result;
		if (resourceTypes == null) {
			result = DEFAULT_ICON.getImageView();
		} else {
			result = findFirst(resourceTypes);
			if (result == null) {
				result = DEFAULT_ICON.getImageView();
			}
		}
		return result;
	}

	private static ImageView findFirst(List<String> resourceTypes) {
		ImageView result = null;
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
