package de.thk.rdw.admin.controller;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.AdminApplication;
import de.thk.rdw.admin.model.TreeItemResource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

public class DashboardController {

	private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

	@FXML
	private ResourceBundle resources;
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
	private HBox notificationContainer;

	private NotificationController notification;

	@FXML
	private void initialize() {
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME, CoAP.COAP_SECURE_URI_SCHEME));
		scheme.getSelectionModel().select(0);

		String path = String.format("%s/%s", AdminApplication.FXML_BASE_FOLDER, "Notification.fxml");
		Node notificationLayout;
		FXMLLoader loader;
		try {
			loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource(path));
			loader.setResources(resources);
			notificationLayout = loader.load();
			notification = loader.getController();
			notificationContainer.getChildren().add(notificationLayout);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@FXML
	private void onActionDiscover() {
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
						notification.success("notification.discovery.success");
						populateTree(response);
					}
				});
			}
		});
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		request.send();
		notification.spinner(resources.getString("notification.discovery.requestSent"));
	}

	@FXML
	private void onActionPing() {
	}

	private void populateTree(Response response) {
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false, true));
	}
}
