package de.thk.ct.admin.tree;

import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.model.GuiCoapResource;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

public class AdvancedTreeCell extends TreeCell<GuiCoapResource> {

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getName());
			setGraphic(new ImageView(item.getImage(IconSize.SMALL)));
		}
	}
}
