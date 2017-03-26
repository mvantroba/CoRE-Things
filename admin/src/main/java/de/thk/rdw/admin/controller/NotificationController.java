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

	public void info(String patternKey, Object... arguments) {
		String messageText = MessageFormat.format(resources.getString(patternKey), arguments);
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-info");
		icon.getChildren().set(0, new ImageView(Icon.INFO_BLUE.getImage(IconSize.MEDIUM)));
		this.message.setText(messageText);
	}

	public void success(String patternKey, Object... arguments) {
		String messageText = MessageFormat.format(resources.getString(patternKey), arguments);
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-success");
		icon.getChildren().set(0, new ImageView(Icon.CHECK_CIRCLE_GREEN.getImage(IconSize.MEDIUM)));
		this.message.setText(messageText);
	}

	public void error(String patternKey, Object... arguments) {
		String messageText = MessageFormat.format(resources.getString(patternKey), arguments);
		fadeIn.playFromStart();
		box.getStyleClass().set(2, "notification-error");
		icon.getChildren().set(0, new ImageView(Icon.ERROR_RED.getImage(IconSize.MEDIUM)));
		this.message.setText(messageText);
	}

	public void spinnerInfo(String patternKey, Object... arguments) {
		spinner(patternKey, "notification-info", arguments);
	}

	public void spinnerWarning(String patternKey, Object... arguments) {
		spinner(patternKey, "notification-warning", arguments);
	}

	private void spinner(String patternKey, String styleClass, Object... arguments) {
		// Prepare nodes for spinner.
		String messageText = MessageFormat.format(resources.getString(patternKey), arguments);
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxWidth(icon.getWidth());
		indicator.setMaxHeight(icon.getHeight());
		// Start spinner.
		fadeIn.playFromStart();
		box.getStyleClass().set(2, styleClass);
		icon.getChildren().set(0, indicator);
		this.message.setText(messageText);
	}
}
