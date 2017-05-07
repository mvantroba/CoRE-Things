package de.thk.ct.admin.controller.tabs;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import de.thk.ct.admin.icon.Icon;
import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.model.GuiCoapResource;
import de.thk.ct.admin.usecase.MainUseCase;
import de.thk.ct.base.ActuatorType;
import de.thk.ct.base.SensorType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public abstract class EndpointResourceCell extends ListCell<GuiCoapResource> {

	private final StackPane root = new StackPane();
	private final ImageView imageView = new ImageView();
	private final Label path = new Label();
	private final Label type = new Label();
	private final Label state = new Label();
	private final Button button = new Button();
	private boolean observing = false;

	public EndpointResourceCell() {
		path.setStyle("-fx-font-weight: bold;");
		VBox vBox = new VBox(path, type);
		HBox hBox = new HBox(8.0, imageView, vBox);
		StackPane.setAlignment(hBox, Pos.CENTER_LEFT);
		StackPane.setAlignment(button, Pos.CENTER_RIGHT);
		button.setGraphic(new ImageView(Icon.POWER_SETTINGS_NEW_GREY.getImage(IconSize.SMALL)));
		button.setOnAction(event -> onToggle(getItem()));
		StackPane.setAlignment(state, Pos.CENTER_RIGHT);
		state.setStyle("-fx-font-weight: bold;");
		state.setTextFill(Color.GREY);
		root.getChildren().addAll(hBox, state, button);
		root.setStyle("-fx-padding: 7.5px 0.0px;");
	}

	public abstract void onToggle(GuiCoapResource item);

	public abstract MainUseCase getUseCase();

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		if (item == null || empty || !item.getAttributes().containsAttribute("rt")
				|| (!ActuatorType.containsTypes(item.getAttributes().getAttributeValues("rt"))
						&& !SensorType.containsTypes(item.getAttributes().getAttributeValues("rt")))) {
			setGraphic(null);
			setGraphic(root);
			button.setVisible(false);
		} else {
			if (ActuatorType.containsTypes(item.getAttributes().getAttributeValues("rt"))) {
				button.setVisible(true);
			} else {
				button.setVisible(false);
			}
			imageView.setImage(item.getImage(IconSize.MEDIUM));
			path.setText(item.getRelativePath());

			String rt = item.getAttributeValues("rt");
			if (rt.split("\\.").length == 3) {
				rt = rt.split("\\.")[2];
			}

			type.setText("Type: ." + rt);
			setGraphic(root);
			setStyle("-fx-border-width: 0px 0px 2.0px 0px; -fx-border-color: #e0e0e0;");
			if (!observing) {
				String uri = item.getEndpointUri() + item.getRelativePath();
				getUseCase().coapObserve(uri, new CoapHandler() {

					@Override
					public void onLoad(CoapResponse response) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								state.setText(response.getResponseText());
								if (response.getResponseText().equals("LOW")) {
									state.setTextFill(Color.GREY);
								} else {
									state.setTextFill(Color.GREEN);
								}
							}
						});
					}

					@Override
					public void onError() {
					}
				});
			}
		}
	}
}
