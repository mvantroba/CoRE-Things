package de.thk.ct.admin.tree;

import java.util.ResourceBundle;

import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.model.GuiCoapResource;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

public abstract class DashboardTreeCell extends TreeCell<GuiCoapResource> {

	private final ContextMenu menu;
	private final ResourceBundle resources;

	public DashboardTreeCell(ResourceBundle resources) {
		this.menu = new ContextMenu();
		this.resources = resources;
		MenuItem showOnPanelA = new MenuItem(this.resources.getString("tree.menu.showOnPanelA"));
		MenuItem showOnPanelB = new MenuItem(this.resources.getString("tree.menu.showOnPanelB"));
		showOnPanelA.setOnAction(event -> onShowOnPanelA(getItem()));
		showOnPanelB.setOnAction(event -> onShowOnPanelB(getItem()));
		this.menu.getItems().add(showOnPanelA);
		this.menu.getItems().add(showOnPanelB);
	}

	protected abstract void onShowOnPanelA(GuiCoapResource guiCoapResource);

	protected abstract void onShowOnPanelB(GuiCoapResource guiCoapResource);

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (item.isEndpoint()) {
				setText(String.format("%s [%s]", item.getName(), resources.getString("tree.endpoint")));
				setStyle("-fx-font-weight: bold;");
				setContextMenu(menu);
			} else {
				setText(item.getName());
				setContextMenu(null);
				setStyle(null);
			}
			setGraphic(new ImageView(item.getImage(IconSize.SMALL)));
		}
	}
}
