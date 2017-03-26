package de.thk.rdw.admin.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.eclipse.californium.core.coap.CoAP;

import de.thk.rdw.admin.model.CoapConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class TargetController {

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private ComboBox<CoapConnection> coapConnection;
	@FXML
	private ChoiceBox<String> scheme;
	@FXML
	private TextField host;
	@FXML
	private TextField port;
	@FXML
	private Button discover;
	@FXML
	private Button ping;
	@FXML
	private Button save;

	@FXML
	public void initialize() {
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME));
		scheme.getSelectionModel().selectFirst();
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public void setCoapConnections(ObservableList<CoapConnection> coapConnections) {
		coapConnection.setItems(coapConnections);
		coapConnection.valueProperty().addListener((observable, oldValue, newValue) -> {
			host.setText(newValue.getHost());
			port.setText(String.valueOf(newValue.getPort()));
		});
	}

	public void initValidation(ValidationSupport validationSupport) {
		// Make "host" required.
		validationSupport.registerValidator(host, Validator.createEmptyValidator(
				MessageFormat.format(resources.getString("validation.empty"), new Object[] { host.getId() })));
		// Make "port" required.
		validationSupport.registerValidator(port, Validator.createEmptyValidator(
				MessageFormat.format(resources.getString("validation.empty"), new Object[] { port.getId() })));
		// Make sure "port" is a valid port number.
		validationSupport.registerValidator(port, Validator.createRegexValidator(
				MessageFormat.format(resources.getString("validation.invalid"), new Object[] { port.getId() }),
				"^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$", Severity.ERROR));
		// Enabled buttons only if the input is valid.
		discover.disableProperty().bind(validationSupport.invalidProperty());
		ping.disableProperty().bind(validationSupport.invalidProperty());
		save.disableProperty().bind(validationSupport.invalidProperty());
	}

	public String getURI() {
		String result = null;
		URI uri;
		try {
			uri = new URI(scheme.getSelectionModel().getSelectedItem(), null, host.getText(), getPort(), null, null,
					null);
			result = uri.toString();
		} catch (URISyntaxException e) {
		}
		return result;
	}

	@FXML
	private void onActionDiscover() {
		mainController.coapDiscover();
	}

	@FXML
	private void onActionPing() {
		mainController.coapPing();
	}

	@FXML
	private void onActionSave() {
		// TODO Implement this method.
	}

	private int getPort() {
		int result = -1;
		try {
			result = Integer.parseInt(port.getText());
		} catch (NumberFormatException e) {
		}
		return result;
	}
}
