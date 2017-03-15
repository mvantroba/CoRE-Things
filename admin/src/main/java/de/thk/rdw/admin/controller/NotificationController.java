package de.thk.rdw.admin.controller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class NotificationController {

	@FXML
	private ResourceBundle resources;
	@FXML
	private Pane pane;
	@FXML
	private Pane icon;
	@FXML
	private Label message;

	private FadeTransition fadeIn;

	@FXML
	private void initialize() {
		fadeIn = new FadeTransition(Duration.millis(500.0), pane);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
	}

	public void info(String message) {
		fadeIn.playFromStart();
		pane.getStyleClass().set(2, "notification-info");
		icon.getChildren().set(0, Icon.INFO_BLUE_36.getImageView());
		this.message.setText(message);
	}

	public void success(String message) {
		fadeIn.playFromStart();
		pane.getStyleClass().set(2, "notification-success");
		icon.getChildren().set(0, Icon.CHECK_CIRCLE_GREEN_36.getImageView());
		this.message.setText(message);
	}

	public void error(String message) {
		fadeIn.playFromStart();
		pane.getStyleClass().set(2, "notification-error");
		icon.getChildren().set(0, Icon.ERROR_RED_36.getImageView());
		this.message.setText(message);
	}

	public void spinner(String pattern, Object... arguments) {
		spinner(MessageFormat.format(pattern, arguments));
	}

	public void spinner(String message) {
		fadeIn.playFromStart();
		pane.getStyleClass().set(2, "notification-info");
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxWidth(icon.getWidth());
		indicator.setMaxHeight(icon.getHeight());
		icon.getChildren().set(0, indicator);
		this.message.setText(message);
	}
}
