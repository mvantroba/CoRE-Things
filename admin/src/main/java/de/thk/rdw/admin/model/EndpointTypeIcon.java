package de.thk.rdw.admin.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.thk.rdw.admin.controller.Icon;
import javafx.scene.image.ImageView;

public class EndpointTypeIcon {

	private static final Icon DEFAULT_ICON = Icon.ENDPOINT_GREY_16;
	private static final Map<EndpointType, Icon> ENDPOINT_TYPE_ICONS = new EnumMap<>(EndpointType.class);

	static {
		ENDPOINT_TYPE_ICONS.put(EndpointType.RASPBERRY_PI, Icon.RASPBERRY_16);
		ENDPOINT_TYPE_ICONS.put(EndpointType.OPENHAB, Icon.OPENHAB_16);
	}

	public static ImageView get(EndpointType endpointType) {
		ImageView result;
		if (ENDPOINT_TYPE_ICONS.containsKey(endpointType)) {
			result = ENDPOINT_TYPE_ICONS.get(endpointType).getImageView();
		} else {
			result = DEFAULT_ICON.getImageView();
		}
		return result;
	}

	public static ImageView get(List<String> endpointTypes) {
		ImageView result;
		if (endpointTypes == null) {
			result = DEFAULT_ICON.getImageView();
		} else {
			result = findFirst(endpointTypes);
			if (result == null) {
				result = DEFAULT_ICON.getImageView();
			}
		}
		return result;
	}

	private static ImageView findFirst(List<String> endpointTypes) {
		ImageView result = null;
		boolean found = false;
		for (String endpointType : endpointTypes) {
			for (Entry<EndpointType, Icon> entry : ENDPOINT_TYPE_ICONS.entrySet()) {
				if (endpointType.equals(entry.getKey().getName())) {
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
