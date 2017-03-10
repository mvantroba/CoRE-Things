package de.thk.rdw.admin.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
				LOGGER.log(Level.INFO, "Received response: \"{0}\".", response.toString());
			}
		});
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\".", uri);
		request.send();
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
