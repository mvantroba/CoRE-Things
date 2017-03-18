package de.thk.rdw.admin.controller;

import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;

public abstract class CoapResourceCell extends TreeCell<GuiCoapResource> {

	private static final Icon ROOT_ICON = Icon.HOME_GREEN;

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
			setGraphic(item.getIcon(IconSize.SMALL));
		}
	}
}
