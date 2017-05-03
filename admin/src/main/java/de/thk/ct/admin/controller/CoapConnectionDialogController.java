package de.thk.ct.admin.controller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.eclipse.californium.core.coap.CoAP;

import de.thk.ct.admin.model.CoapConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CoapConnectionDialogController {

	private Stage dialogStage;
	private boolean okClicked;
	private CoapConnection coapConnection;

	@FXML
	private ResourceBundle resources;
	@FXML
	private Button ok;
	@FXML
	private TextField name;
	@FXML
	private ChoiceBox<String> scheme;
	@FXML
	private TextField host;
	@FXML
	private TextField port;

	@FXML
	private void initialize() {
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME));
		scheme.getSelectionModel().selectFirst();
		initValidation();
	}

	@FXML
	private void onActionOk() {
		coapConnection.setName(name.getText());
		coapConnection.setScheme(scheme.getSelectionModel().getSelectedItem());
		coapConnection.setHost(host.getText());
		coapConnection.setPort(Integer.parseInt(port.getText()));
		okClicked = true;
		dialogStage.close();
	}

	@FXML
	private void onActionCancel() {
		dialogStage.close();
	}

	public void setCoapConnection(CoapConnection coapConnection) {
		this.coapConnection = coapConnection;
		name.setText(coapConnection.getName());
		host.setText(coapConnection.getHost());
		port.setText(String.valueOf(coapConnection.getPort()));
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	private void initValidation() {
		ValidationSupport validationSupport = new ValidationSupport();
		// Make "name" required.
		validationSupport.registerValidator(name, Validator.createEmptyValidator(
				MessageFormat.format(resources.getString("validation.empty"), new Object[] { name.getId() })));
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
		// Enabled button only if the input is valid.
		ok.disableProperty().bind(validationSupport.invalidProperty());
	}
}
