package de.thk.rdw.admin.controller;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.EndpointTypeIcon;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.model.ResourceTypeIcon;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class TreeUtils {

	public static TreeItem<GuiCoapResource> parseResources(Response response, boolean onlyRd, boolean showIcons) {
		TreeItem<GuiCoapResource> result;
		GuiCoapResource home = new GuiCoapResource(
				String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()));
		if (showIcons) {
			result = new TreeItem<>(home, Icon.HOME_GREEN_16.getImageView());
		} else {
			result = new TreeItem<>(home);
		}
		result.setExpanded(true);
		addResources(result, response.getPayloadString(), onlyRd, showIcons);
		return result;
	}

	private static void addResources(TreeItem<GuiCoapResource> rootItem, String payloadString, boolean onlyRd,
			boolean showIcons) {
		Set<WebLink> links = LinkFormat.parse(payloadString);
		TreeItem<GuiCoapResource> parent = rootItem;
		for (WebLink link : links) {
			Scanner scanner = new Scanner(link.getURI());
			scanner.useDelimiter("/");
			while (scanner.hasNext()) {
				String resourceName = scanner.next();
				TreeItem<GuiCoapResource> resource = findChildResource(parent, resourceName);
				if (resource == null) {
					resource = new TreeItem<>(new GuiCoapResource(resourceName));
					resource.setExpanded(true);
					if (showIcons) {
						resource.setGraphic(getGraphic(parent, link, resourceName));
					}
					if (parent.getValue().getName().equals("rd")) {
						resource.setExpanded(false);
					}
					parent.getChildren().add(resource);
				}
				parent = resource;
			}
			parent = rootItem;
			scanner.close();
		}
	}

	private static Node getGraphic(TreeItem<GuiCoapResource> parent, WebLink link, String resourceName) {
		ImageView result = null;
		if (link.getAttributes().containsAttribute("rt")) {
			result = ResourceTypeIcon.get(link.getAttributes().getAttributeValues("rt"));
		} else if (resourceName.equals(".well-known")) {
			result = Icon.PUBLIC_BLUE_16.getImageView();
		} else if (parent.getValue().getName().equals("rd")) {
			result = EndpointTypeIcon.get(link.getAttributes().getAttributeValues("et"));
		}
		if (result == null) {
			result = Icon.RESOURCE_GREY_16.getImageView();
		}
		return result;
	}

	private static TreeItem<GuiCoapResource> findChildResource(TreeItem<GuiCoapResource> parent, String resourceName) {
		TreeItem<GuiCoapResource> result = null;
		for (TreeItem<GuiCoapResource> child : parent.getChildren()) {
			if (resourceName.equals(child.getValue().getName())) {
				result = child;
			}
		}
		return result;
	}
}
