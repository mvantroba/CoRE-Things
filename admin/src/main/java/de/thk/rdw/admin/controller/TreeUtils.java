package de.thk.rdw.admin.controller;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.TreeItemResource;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class TreeUtils {

	public static TreeItem<TreeItemResource> parseResources(Response response, boolean onlyRd, boolean showIcons) {
		TreeItem<TreeItemResource> result;
		TreeItemResource home = new TreeItemResource(
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

	private static void addResources(TreeItem<TreeItemResource> rootItem, String payloadString, boolean onlyRd,
			boolean showIcons) {
		Set<WebLink> links = LinkFormat.parse(payloadString);
		TreeItem<TreeItemResource> parent = rootItem;
		for (WebLink link : links) {
			Scanner scanner = new Scanner(link.getURI());
			scanner.useDelimiter("/");
			while (scanner.hasNext()) {
				String resourceName = scanner.next();
				TreeItem<TreeItemResource> resource = findChildResource(parent, resourceName);
				if (resource == null) {
					resource = new TreeItem<>(new TreeItemResource(resourceName));
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

	// TODO Define resource types with icons globally and optimize this method.
	private static Node getGraphic(TreeItem<TreeItemResource> parent, WebLink link, String resourceName) {
		ImageView result = null;
		if (link.getAttributes().containsAttribute("rt")
				&& link.getAttributes().getAttributeValues("rt").contains("core.rd")) {
			result = Icon.FOLDER_AMBER_16.getImageView();
		} else if (link.getAttributes().containsAttribute("rt")
				&& link.getAttributes().getAttributeValues("rt").contains("core.rd-group")) {
			result = Icon.GROUP_WORK_GREY_16.getImageView();
		} else if (link.getAttributes().containsAttribute("rt")
				&& link.getAttributes().getAttributeValues("rt").contains("core.rd-lookup")) {
			result = Icon.PAGEVIEW_GREY_16.getImageView();
		} else if (resourceName.equals(".well-known")) {
			result = Icon.PUBLIC_BLUE_16.getImageView();
		} else if (parent.getValue().getName().equals("rd")) {
			if (link.getAttributes().containsAttribute("et")
					&& link.getAttributes().getAttributeValues("et").contains("raspberrypi")) {
				result = Icon.RASPBERRY_16.getImageView();
			} else if (link.getAttributes().containsAttribute("et")
					&& link.getAttributes().getAttributeValues("et").contains("openhab")) {
				result = Icon.OPENHAB_16.getImageView();
			} else {
				result = Icon.ENDPOINT_GREY_16.getImageView();
			}
		}
		if (result == null) {
			result = Icon.RESOURCE_GREY_16.getImageView();
		}
		return result;
	}

	private static TreeItem<TreeItemResource> findChildResource(TreeItem<TreeItemResource> parent,
			String resourceName) {
		TreeItem<TreeItemResource> result = null;
		for (TreeItem<TreeItemResource> child : parent.getChildren()) {
			if (resourceName.equals(child.getValue().getName())) {
				result = child;
			}
		}
		return result;
	}

}
