package de.thk.rdw.admin.controller.tabs;

import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.base.ActuatorResourceType;
import de.thk.rdw.base.SensorResourceType;
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
		root.setStyle("-fx-padding: 4.0px 0.0px;");
	}

	public abstract void onToggle(GuiCoapResource item);

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		// TODO Define "rt" globally.
		if (item == null || empty || !item.getAttributes().containsAttribute("rt")
				|| (!ActuatorResourceType.containsTypes(item.getAttributes().getAttributeValues("rt"))
						&& !SensorResourceType.containsTypes(item.getAttributes().getAttributeValues("rt")))) {
			setGraphic(null);
		} else {
			if (ActuatorResourceType.containsTypes(item.getAttributes().getAttributeValues("rt"))) {
				button.setVisible(true);
			} else {
				button.setVisible(false);
			}
			imageView.setImage(item.getImage(IconSize.MEDIUM));
			path.setText(item.getRelativePath());
			type.setText("Type: " + item.getName());
			setGraphic(root);
			setStyle("-fx-border-width: 0px 0px 2.0px 0px; -fx-border-color: #e0e0e0;");
		}
	}
}
