package de.thk.ct.admin.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.admin.icon.EndpointTypeIcon;
import de.thk.ct.admin.icon.Icon;
import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.icon.ResourceTypeIcon;
import javafx.scene.image.Image;

public class GuiCoapResource extends CoapResource {

	public GuiCoapResource(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getAttributeValues(String attr) {
		StringBuilder result = new StringBuilder();
		List<String> values = getAttributes().getAttributeValues(attr);
		String separator = ", ";
		if (values == null || values.isEmpty()) {
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

	public List<GuiCoapResource> getLeafNodes() {
		List<GuiCoapResource> result = new ArrayList<>();
		if (getChildren().isEmpty()) {
			result.add(this);
		} else {
			for (Resource child : getChildren()) {
				GuiCoapResource guiChild = (GuiCoapResource) child;
				result.addAll(guiChild.getLeafNodes());
			}
		}
		return result;
	}

	public String getRelativePath() {
		StringBuilder result = new StringBuilder();
		if (!isEndpoint()) {
			result.insert(0, "/" + getName());
			result.insert(0, ((GuiCoapResource) getParent()).getRelativePath());
		}
		return result.toString();
	}

	public String getEndpointUri() {
		if (getAttributes().containsAttribute("con")) {
			return getAttributes().getAttributeValues("con").get(0);
		} else {
			return ((GuiCoapResource) getParent()).getEndpointUri();
		}
	}

	public Image getImage(IconSize iconSize) {
		Image result = null;
		if (getAttributes().containsAttribute("rt")) {
			result = ResourceTypeIcon.get(getAttributes().getAttributeValues("rt")).getImage(iconSize);
		} else if (getName().equals(".well-known")) {
			result = Icon.PUBLIC_BLUE.getImage(iconSize);
		} else if (isEndpoint()) {
			result = EndpointTypeIcon.get(getAttributes().getAttributeValues("et")).getImage(iconSize);
		} else if (isRoot()) {
			result = Icon.HOME_GREEN.getImage(iconSize);
		}
		if (result == null) {
			result = Icon.RESOURCE_GREY.getImage(iconSize);
		}
		return result;
	}
}
