package de.thk.rdw.admin.controller;

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
		return result;
	}
}
