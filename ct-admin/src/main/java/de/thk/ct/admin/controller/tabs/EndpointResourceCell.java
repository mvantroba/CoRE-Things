package de.thk.ct.admin.controller.tabs;

import java.util.List;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.LinkFormat;

import de.thk.ct.admin.icon.Icon;
import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.model.ActuatorType;
import de.thk.ct.admin.model.GuiCoapResource;
import de.thk.ct.admin.model.SensorType;
import de.thk.ct.admin.usecase.MainUseCase;
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

	private String name = "undefined";

	private final StackPane root = new StackPane();
	private final StackPane emptyRoot = new StackPane();
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
		emptyRoot.setPrefHeight(50);
	}

	public abstract void onToggle(GuiCoapResource item);

	public abstract MainUseCase getUseCase();

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		name = "null";
		if (!shouldRenderResource(item, empty)) {
			setGraphic(emptyRoot);
		} else {
			name = item.getName();
			String rt = item.getAttributes().getAttributeValues(LinkFormat.RESOURCE_TYPE).get(0);
			ActuatorType actuatorType = ActuatorType.get(rt);

			if (actuatorType != null && actuatorType.equals(ActuatorType.LED)) {
				button.setVisible(true);
			} else {
				button.setVisible(false);
			}
			imageView.setImage(item.getImage(IconSize.MEDIUM));
			path.setText(item.getRelativePath());

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
								if (uri.contains(name)) {
									state.setText(response.getResponseText());
									if (response.getResponseText().equals("LOW")) {
										state.setTextFill(Color.GREY);
									} else {
										state.setTextFill(Color.GREEN);
									}
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

	private boolean shouldRenderResource(GuiCoapResource item, boolean empty) {
		boolean result = false;
		List<String> resourceTypes;
		// Prevent null pointer exception.
		if (item != null && !empty && item.getAttributes().containsAttribute(LinkFormat.RESOURCE_TYPE)) {
			// Read assigned resource types.
			resourceTypes = item.getAttributes().getAttributeValues(LinkFormat.RESOURCE_TYPE);
			// Check if one of the resource types is either a sensor or an
			// actuator.
			if (SensorType.containsTypes(resourceTypes) || ActuatorType.containsTypes(resourceTypes)) {
				result = true;
			}
		}
		return result;
	}
}
