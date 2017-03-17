package de.thk.rdw.admin.controller;

import java.util.Scanner;
import java.util.Set;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Response;

import javafx.scene.control.TreeItem;

public class TreeUtils {

	public static TreeItem<CoapResource> parseResources(Response response, boolean onlyRd) {
		TreeItem<CoapResource> rootIem;
		CoapResource rootResource = new CoapResource(
				String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()));
		rootIem = new TreeItem<>(rootResource);
		rootIem.setExpanded(true);
		addResources(rootIem, response.getPayloadString(), onlyRd);
		return rootIem;
	}

	private static void addResources(TreeItem<CoapResource> rootItem, String payloadString, boolean onlyRd) {
		Set<WebLink> links = LinkFormat.parse(payloadString);
		TreeItem<CoapResource> parent = rootItem;
		for (WebLink link : links) {
			Scanner scanner = new Scanner(link.getURI());
			scanner.useDelimiter("/");
			while (scanner.hasNext()) {
				String resourceName = scanner.next();
				TreeItem<CoapResource> resource = findChildResource(parent, resourceName);
				if (resource == null) {
					CoapResource coapResource = new CoapResource(resourceName);
					for (String attr : link.getAttributes().getAttributeKeySet()) {
						for (String value : link.getAttributes().getAttributeValues(attr)) {
							coapResource.getAttributes().addAttribute(attr, value);
						}
					}
					resource = new TreeItem<>(coapResource);
					resource.setExpanded(true);
					parent.getValue().add(coapResource);
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

	private static TreeItem<CoapResource> findChildResource(TreeItem<CoapResource> parent, String resourceName) {
		TreeItem<CoapResource> result = null;
		for (TreeItem<CoapResource> child : parent.getChildren()) {
			if (resourceName.equals(child.getValue().getName())) {
				result = child;
			}
		}
		return result;
	}
}
