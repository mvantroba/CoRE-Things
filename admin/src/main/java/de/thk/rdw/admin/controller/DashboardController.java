package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.TreeItemResource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

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
	private NotificationController notificationController;

	@FXML
	private void initialize() {
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME, CoAP.COAP_SECURE_URI_SCHEME));
		scheme.getSelectionModel().select(0);
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
						notificationController.success("notification.discovery.success");
						populateTree(response);
					}
				});
			}
		});
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		request.send();
		notificationController.spinner(resources.getString("notification.discovery.requestSent"));
	}

	@FXML
	private void onActionPing() {
	}

	private void populateTree(Response response) {
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false, true));
	}
}
