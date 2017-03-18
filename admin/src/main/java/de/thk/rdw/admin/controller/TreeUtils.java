package de.thk.rdw.admin.controller;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.scene.control.TreeItem;

public class TreeUtils {

	public static TreeItem<GuiCoapResource> parseResources(Response response, boolean onlyRd) {
		TreeItem<GuiCoapResource> rootIem;
		GuiCoapResource rootResource = new GuiCoapResource(
				String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()));
		rootIem = new TreeItem<>(rootResource);
		rootIem.setExpanded(true);
		addResources(rootIem, response.getPayloadString(), onlyRd);
		return rootIem;
	}

	private static void addResources(TreeItem<GuiCoapResource> rootItem, String payloadString, boolean onlyRd) {
		Set<WebLink> links = LinkFormat.parse(payloadString);
		TreeItem<GuiCoapResource> parentItem = rootItem;
		boolean resourceIsRd = false;
		for (WebLink link : links) {
			if (link.getURI().equals("/rd") || link.getURI().startsWith("/rd/")) {
				resourceIsRd = true;
			}
			if (onlyRd && !resourceIsRd) {
				continue;
			}
			resourceIsRd = false;
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
					childItem.setExpanded(true);
					parentItem.getValue().add(guiCoapResource);
					if (parentItem.getValue().getName().equals("rd")) {
						childItem.setExpanded(false);
					}
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
