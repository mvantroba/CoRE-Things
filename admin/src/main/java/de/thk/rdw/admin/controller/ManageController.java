package de.thk.rdw.admin.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.TreeItemResource;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ManageController {

	private static final Logger LOGGER = Logger.getLogger(ManageController.class.getName());

	private static final String CLASS_FIELD_ERROR = "text-field-error";

	private Stage primaryStage;

	@FXML
	private ChoiceBox<String> scheme;
	@FXML
	private TextField host;
	@FXML
	private TextField port;
	@FXML
	private TextField variables;
	@FXML
	private TreeView<TreeItemResource> resourceTree;

	@FXML
	private HBox notificationArea;
	@FXML
	private Pane notificationIcon;
	@FXML
	private Label notificationMessage;

	private FadeTransition fadeIn;

	@FXML
	private void initialize() {
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME, CoAP.COAP_SECURE_URI_SCHEME));
		scheme.getSelectionModel().select(0);
		fadeIn = new FadeTransition(Duration.millis(500.0));
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
	}

	@FXML
	private void onActionDiscover() {
		progress("Discovering resources...");
		Request request = new Request(CoAP.Code.GET);
		String uri = String.format("%s://%s:%s/.well-known/core", scheme.getValue(), host.getText(), port.getText());
		request.setURI(uri);
		request.addMessageObserver(new MessageObserverAdapter() {

			@Override
			public void onResponse(Response response) {
				LOGGER.log(Level.INFO, "Received response: \"{0}\".", response.getPayloadString());
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						success("Discovery was successful.");
						populateTree(response);
					}
				});
			}
		});
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\".", uri);
		request.send();
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	private void onActionPing() {
	}

	private void info(String message) {
		fadeIn.setNode(notificationArea);
		fadeIn.playFromStart();
		notificationArea.getStyleClass().set(1, "notification-info");
		notificationIcon.getChildren().set(0, Icon.INFO_BLUE_36.getImageView());
		notificationMessage.setText(message);
	}

	private void success(String message) {
		fadeIn.setNode(notificationArea);
		fadeIn.playFromStart();
		notificationArea.getStyleClass().set(1, "notification-success");
		notificationIcon.getChildren().set(0, Icon.CHECK_CIRCLE_GREEN_36.getImageView());
		notificationMessage.setText(message);
	}

	private void error(String message) {
		fadeIn.setNode(notificationArea);
		fadeIn.playFromStart();
		notificationArea.getStyleClass().set(1, "notification-error");
		notificationIcon.getChildren().set(0, Icon.ERROR_RED_36.getImageView());
		notificationMessage.setText(message);
	}

	private void progress(String message) {
		fadeIn.setNode(notificationArea);
		fadeIn.playFromStart();
		notificationArea.getStyleClass().set(1, "notification-info");
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxWidth(notificationIcon.getWidth());
		indicator.setMaxHeight(notificationIcon.getHeight());
		notificationIcon.getChildren().set(0, indicator);
		notificationMessage.setText(message);
	}

	private void populateTree(Response response) {
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false, true));
	}
}
