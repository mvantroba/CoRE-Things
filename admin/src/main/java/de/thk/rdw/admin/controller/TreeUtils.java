package de.thk.rdw.admin.controller;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.TreeItemResource;
import javafx.scene.control.TreeItem;

public class TreeUtils {

	private static final double ICON_SIZE = 20.0;

	public static TreeItem<TreeItemResource> parseResources(Response response, boolean onlyRd, boolean showIcons) {
		TreeItem<TreeItemResource> result;
		TreeItemResource home = new TreeItemResource(
				String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()));
		if (showIcons) {
			result = new TreeItem<>(home, Icon.HOME_GREEN_LARGE.getImageView(ICON_SIZE));
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
					parent.getChildren().add(resource);
				}
				parent = resource;
			}
			parent = rootItem;
			scanner.close();
		}
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
