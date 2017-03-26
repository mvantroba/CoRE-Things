package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.validation.ValidationSupport;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;

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
		useCase.coapDiscover(uri, new MessageObserverImpl("DISCOVERY", uri) {
			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(() -> {
					notificationController.success("notification.discovery.success");
					dashboardController.populateTree(response);
					advancedController.populateTree(response);
				});
			}
		});
		LOGGER.log(Level.INFO, "DISCOVERY request has been sent to {0}.", uri);
		Platform.runLater(() -> notificationController.spinnerInfo("notification.discovery.requestSent"));
	}

	public void coapPing() {
		String uri = targetController.getURI();
		useCase.coapPing(uri, new MessageObserverImpl("Ping", uri));
	}

	public void coapGET() {
		String uri = targetController.getURI();
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		useCase.coapGET(uri, new MessageObserverImpl(Code.GET.name(), uri));
	}

	public void coapPOST() {
		String uri = targetController.getURI();
		LOGGER.log(Level.INFO, "Sending POST request to \"{0}\"...", uri);
		useCase.coapPOST(uri, "", new MessageObserverImpl(Code.GET.name(), uri), MediaTypeRegistry.TEXT_PLAIN);
	}

	private class MessageObserverImpl implements MessageObserver {

		private String code;
		private String uri;
		private int retransmit;

		public MessageObserverImpl(String code, String uri) {
			this.code = code;
			this.uri = uri;
			this.retransmit = 0;
		}

		@Override
		public void onRetransmission() {
			retransmit++;
			String currentRetransmit = String.format("%d/%d", retransmit,
					NetworkConfig.getStandard().getInt(NetworkConfig.Keys.MAX_RETRANSMIT));

			LOGGER.log(Level.WARNING, "Retransmitting {0} request to {1} ({2}).",
					new Object[] { code, uri, currentRetransmit });

			Platform.runLater(
					() -> notificationController.spinnerWarning("notification.retransmit", currentRetransmit));
		}

		@Override
		public void onResponse(Response response) {
			LOGGER.log(Level.INFO, "Received response from {0}. Code: {1}, Payload: {2}.",
					new Object[] {
							String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()),
							response.getCode(), response.getPayloadString() });
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
			LOGGER.log(Level.SEVERE, "{0} request to {1} timed out.", new Object[] { code, uri });
			Platform.runLater(() -> notificationController.error("notification.requestTimeout"));
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
		}
	}
}
