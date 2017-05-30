package de.thk.ct.admin.icon;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.thk.ct.admin.model.ActuatorType;
import de.thk.ct.admin.model.RdResourceType;
import de.thk.ct.admin.model.SensorType;

public class ResourceTypeIcon {

	private static final Icon DEFAULT_ICON = Icon.RESOURCE_GREY;
	private static final Map<String, Icon> RESOURCE_TYPE_ICONS = new TreeMap<>();

	static {
		// RD
		RESOURCE_TYPE_ICONS.put(RdResourceType.CORE_RD.getType(), Icon.FOLDER_AMBER);
		RESOURCE_TYPE_ICONS.put(RdResourceType.CORE_RD_GROUP.getType(), Icon.GROUP_WORK_GREY);
		RESOURCE_TYPE_ICONS.put(RdResourceType.CORE_RD_LOOKUP.getType(), Icon.PAGEVIEW_GREY);
		// Sensors
		RESOURCE_TYPE_ICONS.put(SensorType.MERCURY_SWITCH.getType(), Icon.SCREEN_ROTATION);
		RESOURCE_TYPE_ICONS.put(SensorType.TACTILE_SWITCH.getType(), Icon.PUSH_THE_BUTTON);
		RESOURCE_TYPE_ICONS.put(SensorType.PIR_SENSOR.getType(), Icon.INDUSTRY_INFRARED_SENSOR);
		RESOURCE_TYPE_ICONS.put(SensorType.DS18B20.getType(), Icon.THERMOMETER);
		// Actuators
		RESOURCE_TYPE_ICONS.put(ActuatorType.LED.getType(), Icon.LED_LIGHT_LAMP);
		RESOURCE_TYPE_ICONS.put(ActuatorType.LCD16X2.getType(), Icon.LCD);
	}

	public static Icon get(String type) {
		Icon result;
		if (RESOURCE_TYPE_ICONS.containsKey(type)) {
			result = RESOURCE_TYPE_ICONS.get(type);
		} else {
			result = DEFAULT_ICON;
		}
		return result;
	}

	public static Icon get(List<String> types) {
		Icon result;
		if (types == null) {
			result = DEFAULT_ICON;
		} else {
			result = findFirst(types);
			if (result == null) {
				result = DEFAULT_ICON;
			}
		}
		return result;
	}

	private static Icon findFirst(List<String> types) {
		Icon result = null;
		boolean found = false;
		for (String resourceType : types) {
			for (Entry<String, Icon> entry : RESOURCE_TYPE_ICONS.entrySet()) {
				if (resourceType.equals(entry.getKey())) {
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
