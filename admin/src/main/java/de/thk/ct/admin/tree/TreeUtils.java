package de.thk.ct.admin.tree;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import de.thk.ct.admin.model.GuiCoapResource;
import javafx.scene.control.TreeItem;

public class TreeUtils {

	public static TreeItem<GuiCoapResource> parseResources(Response response) {
		TreeItem<GuiCoapResource> rootIem;
		GuiCoapResource rootResource = new GuiCoapResource(
				String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()));
		rootIem = new TreeItem<>(rootResource);
		rootIem.setExpanded(true);
		addResources(rootIem, response.getPayloadString());
		return rootIem;
	}

	private static void addResources(TreeItem<GuiCoapResource> rootItem, String payloadString) {
		Set<WebLink> links = LinkFormat.parse(payloadString);
		TreeItem<GuiCoapResource> parentItem = rootItem;
		for (WebLink link : links) {
			Scanner scanner = new Scanner(link.getURI());
			scanner.useDelimiter("/");
			while (scanner.hasNext()) {
				String resourceName = scanner.next();
				TreeItem<GuiCoapResource> childItem = findChildResource(parentItem, resourceName);
				if (childItem == null) {
					GuiCoapResource guiCoapResource = new GuiCoapResource(resourceName);
					for (String attr : link.getAttributes().getAttributeKeySet()) {
						for (String value : link.getAttributes().getAttributeValues(attr)) {
							guiCoapResource.getAttributes().addAttribute(attr, value);
						}
					}
					childItem = new TreeItem<>(guiCoapResource);
					if (guiCoapResource.isResourceDirectory()) {
						childItem.setExpanded(true);
					}
					parentItem.getValue().add(guiCoapResource);
					parentItem.getChildren().add(childItem);
				}
				parentItem = childItem;
			}
			parentItem = rootItem;
			scanner.close();
		}
	}

	private static TreeItem<GuiCoapResource> findChildResource(TreeItem<GuiCoapResource> parentItem,
			String resourceName) {
		TreeItem<GuiCoapResource> result = null;
		for (TreeItem<GuiCoapResource> childItem : parentItem.getChildren()) {
			if (resourceName.equals(childItem.getValue().getName())) {
				result = childItem;
			}
		}
		return result;
	}
}
