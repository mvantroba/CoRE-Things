package de.thk.rdw.admin.controller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class NotificationController {

	@FXML
	private ResourceBundle resources;
	@FXML
	private HBox box;
	@FXML
	private Pane icon;
	@FXML
	private Label message;

	private FadeTransition fadeIn;

	@FXML
	private void initialize() {
		fadeIn = new FadeTransition(Duration.millis(500.0), box);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
	}

	public void info(String message) {
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-info");
		icon.getChildren().set(0, new ImageView(Icon.INFO_BLUE.getImage(IconSize.MEDIUM)));
		this.message.setText(message);
	}

	public void success(String message) {
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-success");
		icon.getChildren().set(0, new ImageView(Icon.CHECK_CIRCLE_GREEN.getImage(IconSize.MEDIUM)));
		this.message.setText(message);
	}

	public void error(String message) {
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-error");
		icon.getChildren().set(0, new ImageView(Icon.ERROR_RED.getImage(IconSize.MEDIUM)));
		this.message.setText(message);
	}

	public void spinner(String pattern, Object... arguments) {
		spinner(MessageFormat.format(pattern, arguments));
	}

	public void spinner(String message) {
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-info");
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxWidth(icon.getWidth());
		indicator.setMaxHeight(icon.getHeight());
		icon.getChildren().set(0, indicator);
		this.message.setText(message);
	}
}
