package de.thk.rdw.admin.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.CoapConnection;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class TargetController implements Initializable {

	private static final Logger LOGGER = Logger.getLogger(TargetController.class.getName());

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private ComboBox<CoapConnection> connection;
	@FXML
	private ChoiceBox<String> scheme;
	@FXML
	private TextField host;
	@FXML
	private TextField port;

	private ObservableList<CoapConnection> connections;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		connections = FXCollections.observableArrayList();
		connection.setItems(connections);
		connection.valueProperty().addListener(new ChangeListener<CoapConnection>() {

			@Override
			public void changed(ObservableValue<? extends CoapConnection> observable, CoapConnection oldValue,
					CoapConnection newValue) {
				updateData(newValue);
			}
		});
		scheme.setItems(FXCollections.observableArrayList(CoAP.COAP_URI_SCHEME));
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
						mainController.success("notification.discovery.success");
						mainController.populateTree(response);
					}
				});
			}
		});
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		request.send();
		mainController.spinner("notification.discovery.requestSent");
	}

	@FXML
	private void onActionPing() {
	}

	@FXML
	private void onActionSave() {
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public String getHost() {
		return host.getText();
	}

	public int getPort() {
		int result = -1;
		try {
			result = Integer.parseInt(port.getText());
		} catch (NumberFormatException e) {
			// TODO Show notification.
		}
		return result;
	}

	public void updateConnections(List<CoapConnection> coapConnections) {
		connections.setAll(coapConnections);
	}

	private void updateData(CoapConnection newValue) {
		host.textProperty().bindBidirectional(newValue.getHostProperty());
		port.textProperty().bindBidirectional(newValue.getPortProperty(), new NumberStringConverter("#") {
			@Override
			public Number fromString(String value) {
				Number result = null;
				try {
					result = super.fromString(value);
				} catch (RuntimeException e) {
					LOGGER.log(Level.INFO, "Bad input");
				}
				return result;
			}
		});
	}
}
