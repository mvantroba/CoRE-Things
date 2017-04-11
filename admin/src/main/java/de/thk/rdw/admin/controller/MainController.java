package de.thk.rdw.admin.controller;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;

import de.thk.rdw.admin.AdminApplication;
import de.thk.rdw.admin.controller.tabs.AdvancedController;
import de.thk.rdw.admin.controller.tabs.ConnectionsController;
import de.thk.rdw.admin.controller.tabs.DashboardController;
import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.model.CoapConnection;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.TreeUtils;
import de.thk.rdw.admin.usecase.MainUseCase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	private MainUseCase useCase;
	private Stage primaryStage;
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
	private ConnectionsController connectionsController;

	@FXML
	private void initialize() {
		ValidationSupport validationSupport = new ValidationSupport();
		targetController.initValidation(validationSupport);
		targetController.setMainController(this);
		dashboardController.setMainController(this);
		advancedController.setMainController(this);
		connectionsController.setMainController(this);
	}

	public void setUseCase(MainUseCase useCase) {
		this.useCase = useCase;
		coapConnections = FXCollections.observableArrayList(useCase.findAllCoapConnections(true));
		targetController.setCoapConnections(coapConnections);
		connectionsController.setCoapConnections(coapConnections);
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void coapDiscover() {
		String uri = targetController.getURI();
		useCase.coapDiscover(uri, new MessageObserverImpl("DISCOVERY", uri) {
			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(() -> {
					notificationController.success("notification.discovery.success");
					TreeItem<GuiCoapResource> rootItem = TreeUtils.parseResources(response);
					dashboardController.populateTree(rootItem);
					advancedController.populateTree(rootItem);
					// TODO Define this resource type globally.
					if (response.getPayloadString().contains("core.rd")) {
						coapObserve("coap://" + response.getSource().getHostAddress() + ":" + response.getSourcePort()
								+ "/rd");
					}
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

	public void coapObserve(String uri) {
		LOGGER.log(Level.INFO, "Sending OBSERVE request to \"{0}\"...", uri);
		useCase.coapObserve(uri, new CoapHandler() {

			private boolean receivedFirstResponse = false;
			private int observeFlag = 0;

			@Override
			public void onLoad(CoapResponse response) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						LOGGER.log(Level.INFO, "Received update from {0}: \"{1}\"", new Object[] {
								response.advanced().getSource().toString(), response.advanced().toString() });
						Integer currentObserveFlag = response.advanced().getOptions().getObserve();
						// Only show notification and update tree if the
						// resource was updated (observe flag has been
						// incremented) and it is not the first observer
						// response.
						if (currentObserveFlag > observeFlag && receivedFirstResponse) {
							observeFlag = currentObserveFlag;
							Notifications.create().title("Resource Directory") //
									.text("Endpoint list has been updated.") //
									.graphic(new ImageView(Icon.INFO_BLUE.getImage(IconSize.MEDIUM))) //
									.position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).show();

							// TODO Update only rd resource instead of doing
							// full discovery request.
							useCase.coapDiscover(uri, new MessageObserverImpl("DISCOVERY", uri) {
								@Override
								public void onResponse(Response response) {
									super.onResponse(response);
									Platform.runLater(() -> {
										TreeItem<GuiCoapResource> rootItem = TreeUtils.parseResources(response);
										dashboardController.populateTree(rootItem);
										advancedController.populateTree(rootItem);
									});
								}
							});
						} else {
							receivedFirstResponse = true;
						}
					}
				});
			}

			@Override
			public void onError() {
				// TODO Cancel observation, show notification, reset GUI.
			}
		});
	}

	public boolean showCoapConnectionDialog(CoapConnection coapConnection) {
		boolean result = false;
		try {
			// Load layout.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AdminApplication.class.getResource("/fxml/CoapConnectionDialog.fxml"));
			loader.setResources(resources);
			VBox dialogRoot = loader.load();
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(resources.getString("dialog.editConnection"));
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(false);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(dialogRoot);
			dialogStage.setScene(scene);
			// Set the CoAP connection into the controller.
			CoapConnectionDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCoapConnection(coapConnection);
			// Show the dialog and wait until the user closes it.
			dialogStage.showAndWait();
			result = controller.isOkClicked();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public void saveCoapConnection(CoapConnection coapConnection) {
		if (useCase.findCoapConnectionById(coapConnection) == null) {
			useCase.createCoapConnection(coapConnection);
			notificationController.success("notification.connection.createSuccess", coapConnection.getName());
		} else {
			useCase.updateCoapConnection(coapConnection);
			notificationController.success("notification.connection.updateSuccess", coapConnection.getName());
		}
		coapConnections.setAll(useCase.findAllCoapConnections(false));
	}

	public void deleteCoapConnection(CoapConnection coapConnection) {
		useCase.deleteCoapConnection(coapConnection);
		notificationController.success("notification.connection.deleteSuccess", coapConnection.getName());
		coapConnections.setAll(useCase.findAllCoapConnections(false));
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
