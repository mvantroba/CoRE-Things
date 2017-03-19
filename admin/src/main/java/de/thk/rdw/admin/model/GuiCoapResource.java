package de.thk.rdw.admin.model;

import java.util.List;

import org.eclipse.californium.core.CoapResource;

import de.thk.rdw.admin.icon.EndpointTypeIcon;
import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.icon.ResourceTypeIcon;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class GuiCoapResource extends CoapResource {

	public GuiCoapResource(String name) {
		super(name);
	}

	public String getAttributeValues(String attr) {
		StringBuilder result = new StringBuilder();
		List<String> values = getAttributes().getAttributeValues(attr);
		String separator = ", ";
		if (values == null || values.isEmpty()) {
			// TODO Localize this String.
			result.append("undefined");
		} else {
			for (int i = 0; i < values.size(); i++) {
				result.append(values.get(i));
				if (i < values.size() - 1) {
					result.append(separator);
				}
			}
		}
		return result.toString();
	}

	public boolean isRoot() {
		return getParent() == null;
	}

	public boolean isEndpoint() {
		return getAttributes().containsAttribute("con");
	}

	public boolean isResourceDirectory() {
		return getAttributes().containsAttribute("rt") && getAttributes().getAttributeValues("rt").contains("core.rd");
	}

	public Node getIcon(IconSize iconSize) {
		ImageView result = null;
		// TODO Define resource attributes globally.
		if (getAttributes().containsAttribute("rt")) {
			result = ResourceTypeIcon.get(getAttributes().getAttributeValues("rt")).getImageView(iconSize);
		} else if (getName().equals(".well-known")) {
			result = Icon.PUBLIC_BLUE.getImageView(iconSize);
		} else if (isEndpoint()) {
			result = EndpointTypeIcon.get(getAttributes().getAttributeValues("et")).getImageView(iconSize);
		} else if (isRoot()) {
			result = Icon.HOME_GREEN.getImageView(iconSize);
		}
		if (result == null) {
			result = Icon.RESOURCE_GREY.getImageView(iconSize);
		}
		return result;
	}
}
