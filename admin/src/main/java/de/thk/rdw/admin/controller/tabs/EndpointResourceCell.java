package de.thk.rdw.admin.controller.tabs;

import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;

public class EndpointResourceCell extends ListCell<GuiCoapResource> {

	private final StackPane root;
	private final Label label;
	private final Button button;

	public EndpointResourceCell() {
		root = new StackPane();
		// TODO Localize messages.
		label = new Label("empty");
		StackPane.setAlignment(label, Pos.CENTER_LEFT);
		button = new Button("toggle");
		StackPane.setAlignment(button, Pos.CENTER_RIGHT);
		root.getChildren().addAll(label, button);
	}

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		if (item == null || empty) {
			setGraphic(null);
		} else {
			label.setText(item.getName());
			setGraphic(root);
		}
	}
}
