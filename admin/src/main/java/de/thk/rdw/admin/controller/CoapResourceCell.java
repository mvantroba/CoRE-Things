package de.thk.rdw.admin.controller;

import de.thk.rdw.admin.model.EndpointTypeIcon;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.model.ResourceTypeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

public abstract class CoapResourceCell extends TreeCell<GuiCoapResource> {

	private static final Icon ROOT_ICON = Icon.HOME_GREEN_16;

	private ContextMenu menu = new ContextMenu();

	public CoapResourceCell() {
		// TODO Read text from bundle.
		MenuItem show = new MenuItem("Show");
		show.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onShowAction(getItem());
			}
		});
		menu.getItems().add(show);
	}

	protected abstract void onShowAction(GuiCoapResource guiCoapResource);

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getName());
			setContextMenu(menu);
			setGraphic(getIcon());
		}
	}

	private Node getIcon() {
		ImageView result = null;
		if (getItem().getAttributes().containsAttribute("rt")) {
			result = ResourceTypeIcon.get(getItem().getAttributes().getAttributeValues("rt"));
		} else if (getItem().getName().equals(".well-known")) {
			result = Icon.PUBLIC_BLUE_16.getImageView();
		} else if (getItem().getParent() != null && getItem().getParent().getName().equals("rd")) {
			result = EndpointTypeIcon.get(getItem().getAttributes().getAttributeValues("et"));
		} else if (getItem().getParent() == null) {
			result = ROOT_ICON.getImageView();
		}
		if (result == null) {
			result = Icon.RESOURCE_GREY_16.getImageView();
		}
		return result;
	}
}
