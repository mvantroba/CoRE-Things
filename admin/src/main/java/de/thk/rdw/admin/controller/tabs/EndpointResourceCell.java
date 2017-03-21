package de.thk.rdw.admin.controller.tabs;

import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public abstract class EndpointResourceCell extends ListCell<GuiCoapResource> {

	private final StackPane root = new StackPane();
	private final ImageView imageView = new ImageView();
	private final Label path = new Label();
	private final Label type = new Label();
	private final Button button = new Button();

	public EndpointResourceCell() {
		path.setStyle("-fx-font-weight: bold;");
		VBox vBox = new VBox(path, type);
		HBox hBox = new HBox(8.0, imageView, vBox);
		StackPane.setAlignment(hBox, Pos.CENTER_LEFT);
		StackPane.setAlignment(button, Pos.CENTER_RIGHT);
		button.setGraphic(new ImageView(Icon.POWER_SETTINGS_NEW_GREY.getImage(IconSize.SMALL)));
		button.setOnAction(event -> onToggle(getItem()));
		root.getChildren().addAll(hBox, button);
	}

	public abstract void onToggle(GuiCoapResource item);

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		if (item == null || empty) {
			setGraphic(null);
		} else {
			imageView.setImage(item.getImage(IconSize.MEDIUM));
			path.setText(item.getRelativePath());
			type.setText("Type: " + item.getName());
			setGraphic(root);
		}
	}
}
