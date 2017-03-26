package de.thk.rdw.admin.controller;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.validation.ValidationSupport;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.tabs.AdvancedController;
import de.thk.rdw.admin.controller.tabs.DashboardController;
import de.thk.rdw.admin.model.CoapConnection;
import de.thk.rdw.admin.usecase.MainUseCase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	private MainUseCase useCase;
	private ObservableList<CoapConnection> coapConnections;

	@FXML
	private ResourceBundle resources;
	@FXML
	private HeaderController headerController;
	@FXML
	private TargetController targetController;
	@FXML
	private NotificationController notificationController;
	@FXML
	private DashboardController dashboardController;
	@FXML
	private AdvancedController advancedController;

	@FXML
	private void initialize() {
		ValidationSupport validationSupport = new ValidationSupport();
		targetController.initValidation(validationSupport);
		targetController.setMainController(this);
		dashboardController.setMainController(this);
		advancedController.setMainController(this);
	}

	public void setUseCase(MainUseCase useCase) {
		this.useCase = useCase;
		coapConnections = FXCollections.observableArrayList(useCase.findAllCoapConnections());
		targetController.setCoapConnections(coapConnections);
	}

	public void coapDiscover() {
		String uri = targetController.getURI();
		LOGGER.log(Level.INFO, "Sending Discovery request to \"{0}\"...", uri);
		useCase.coapDiscover(uri, new DiscoverMessageObserver());
		spinner("notification.discovery.requestSent");
	}

	public void coapGET() {
		String uri = targetController.getURI();
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		useCase.coapGET(uri, new DiscoverMessageObserver());
	}

	public void coapPOST() {
		String uri = targetController.getURI();
		LOGGER.log(Level.INFO, "Sending POST request to \"{0}\"...", uri);
		useCase.coapPOST(uri, "", new DiscoverMessageObserver(), MediaTypeRegistry.TEXT_PLAIN);
	}

	public void success(String key) {
		notificationController.success(resources.getString(key));
	}

	public void spinner(String key) {
		notificationController.spinner(resources.getString(key));
	}

	public String getPath() {
		return advancedController.getPath();
	}

	public String getQuery() {
		return advancedController.getQuery();
	}

	private class DiscoverMessageObserver implements MessageObserver {

		@Override
		public void onRetransmission() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResponse(Response response) {
			LOGGER.log(Level.INFO, "Received response: \"{0}\".", response.getPayloadString());
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					success("notification.discovery.success");
					dashboardController.populateTree(response);
					advancedController.populateTree(response);
				}
			});
		}

		@Override
		public void onAcknowledgement() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReject() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTimeout() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}
	}

	public List<CoapConnection> getCoapConnections() {
		return useCase.findAllCoapConnections();
	}

	public void coapPing() {
		// TODO Auto-generated method stub

	}
}
