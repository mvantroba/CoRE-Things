package de.thk.ct.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;

import de.thk.ct.admin.AdminApplication;
import de.thk.ct.admin.controller.tabs.AdvancedController;
import de.thk.ct.admin.controller.tabs.ConnectionsController;
import de.thk.ct.admin.controller.tabs.DashboardController;
import de.thk.ct.admin.controller.tabs.LogController;
import de.thk.ct.admin.icon.Icon;
import de.thk.ct.admin.icon.IconSize;
import de.thk.ct.admin.model.CoapConnection;
import de.thk.ct.admin.model.GuiCoapResource;
import de.thk.ct.admin.model.RdResourceType;
import de.thk.ct.admin.tree.TreeUtils;
import de.thk.ct.admin.usecase.MainUseCase;
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
	private CoapObserveRelation rdObserveRelation = null;

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
	private LogController logController;

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

	public MainUseCase getUseCase() {
		return useCase;
	}

	public void coapDiscover() {
		String uri = targetController.getURI();
		useCase.coapDiscover(uri, new MessageObserverImpl("DISCOVERY", uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(() -> {
					notificationController.success("notification.discovery.success");

					// Reset displayed panels on dashboard.
					dashboardController.resetPanels();

					TreeItem<GuiCoapResource> rootItem = TreeUtils.parseResources(response);
					dashboardController.populateTree(rootItem);
					advancedController.populateTree(rootItem);
					if (response.getPayloadString().contains(RdResourceType.CORE_RD.getType())) {
						// Observe /rd resource.
						String observerUri = String.format("%s://%s:%d/%s", CoAP.COAP_URI_SCHEME,
								response.getSource().getHostAddress(), response.getSourcePort(),
								RdResourceType.CORE_RD.getName());
						coapObserve(observerUri);
					}
				});
			}
		});
		LOGGER.log(Level.INFO, "DISCOVERY request has been sent to {0}.", uri);
		Platform.runLater(() -> notificationController.spinnerInfo("notification.discovery.requestSent"));
	}

	public void coapPing() {
		String uri = targetController.getURI();
		useCase.coapPing(uri, new MessageObserverImpl("Ping", uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(() -> {
					notificationController.success("notification.ping.success");
				});
			}
		});
		Platform.runLater(() -> notificationController.spinnerInfo("notification.ping.requestSent"));
	}

	private String getFullUri() {
		String path = "";
		String query = "";
		if (!advancedController.getPath().isEmpty()) {
			path = "/" + advancedController.getPath();
		}
		if (!advancedController.getQuery().isEmpty()) {
			query = "?" + advancedController.getQuery();
		}
		return targetController.getURI() + path + query;
	}

	public void coapGET() {
		String uri = getFullUri();
		LOGGER.log(Level.INFO, "Sending GET request to \"{0}\"...", uri);
		useCase.coapGET(uri, new MessageObserverImpl(Code.GET.name(), uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						notificationController.success("request.get.responded", response.getCode());
						advancedController.setResponse("Code: " + response.getCode().toString());
						advancedController.setResponsePayload(response.getPayloadString());
					}
				});
			}
		});
		advancedController.setRequest("GET " + uri);
	}

	public void coapPOST() {
		String uri = getFullUri();
		LOGGER.log(Level.INFO, "Sending POST request to \"{0}\"...", uri);
		useCase.coapPOST(uri, advancedController.getRequestPayload(), new MessageObserverImpl(Code.POST.name(), uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						notificationController.success("request.post.responded", response.getCode());
						advancedController.setResponse("Code: " + response.getCode().toString());
						advancedController.setResponsePayload(response.getPayloadString());
					}
				});
			}
		}, MediaTypeRegistry.TEXT_PLAIN);
		advancedController.setRequest("POST " + uri);
	}

	public void coapPUT() {
		String uri = getFullUri();
		LOGGER.log(Level.INFO, "Sending PUT request to \"{0}\"...", uri);
		useCase.coapPUT(uri, advancedController.getRequestPayload(), new MessageObserverImpl(Code.PUT.name(), uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						notificationController.success("request.put.responded", response.getCode());
						advancedController.setResponse("Code: " + response.getCode().toString());
						advancedController.setResponsePayload(response.getPayloadString());
					}
				});
			}
		}, MediaTypeRegistry.TEXT_PLAIN);
		advancedController.setRequest("PUT " + uri);
	}

	public void coapDELETE() {
		String uri = getFullUri();
		LOGGER.log(Level.INFO, "Sending DELETE request to \"{0}\"...", uri);
		useCase.coapDELETE(uri, new MessageObserverImpl(Code.DELETE.name(), uri) {

			@Override
			public void onResponse(Response response) {
				super.onResponse(response);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						notificationController.success("request.delete.responded", response.getCode());
						advancedController.setResponse("Code: " + response.getCode().toString());
						advancedController.setResponsePayload(response.getPayloadString());
					}
				});
			}
		});
		advancedController.setRequest("DELETE " + uri);
	}

	public void coapObserve(String uri) {
		if (rdObserveRelation != null) {
			LOGGER.log(Level.INFO, "Sending OBSERVE CANCEL request...");
			rdObserveRelation.proactiveCancel();
		}
		LOGGER.log(Level.INFO, "Sending OBSERVE request to \"{0}\"...", uri);
		rdObserveRelation = useCase.coapObserve(uri, new CoapHandler() {

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
						// Only show toast and update tree if the
						// resource was updated (observe flag has been
						// incremented) and it is not the first observe
						// response.
						if (currentObserveFlag > observeFlag && receivedFirstResponse) {
							observeFlag = currentObserveFlag;
							Notifications.create().title(resources.getString("toast.resourceDirectory")) //
									.text(resources.getString("toast.endpointsUpdated")) //
									.graphic(new ImageView(Icon.INFO_BLUE.getImage(IconSize.MEDIUM))) //
									.position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).show();

							useCase.coapDiscover(uri, new MessageObserverImpl("DISCOVERY", uri) {
								@Override
								public void onResponse(Response response) {
									super.onResponse(response);
									Platform.runLater(() -> {
										// Send new discovery request and create
										// new tree.
										TreeItem<GuiCoapResource> rootItem = TreeUtils.parseResources(response);
										dashboardController.populateTree(rootItem);
										advancedController.populateTree(rootItem);
									});
								}
							});
						} else {
							receivedFirstResponse = true;
							observeFlag = currentObserveFlag;
						}
					}
				});
			}

			@Override
			public void onError() {
				LOGGER.log(Level.WARNING, "Observe request to {1} has failed.", new Object[] { uri });
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
			LOGGER.log(Level.INFO, "Received response from {0}: {1}",
					new Object[] {
							String.format("%s:%s", response.getSource().getHostAddress(), response.getSourcePort()),
							response.toString() });
		}

		@Override
		public void onAcknowledgement() {
			LOGGER.log(Level.INFO, "{0} request to {1} has been acknowledged.", new Object[] { code, uri });
		}

		@Override
		public void onReject() {
			LOGGER.log(Level.WARNING, "{0} request to {1} has been rejected.", new Object[] { code, uri });
		}

		@Override
		public void onTimeout() {
			LOGGER.log(Level.SEVERE, "{0} request to {1} timed out.", new Object[] { code, uri });
			Platform.runLater(() -> notificationController.error("notification.requestTimeout"));
		}

		@Override
		public void onCancel() {
			LOGGER.log(Level.WARNING, "{0} request to {1} has been canceled.", new Object[] { code, uri });
		}
	}

	public OutputStream getLogStream() {
		return new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				throw new IOException("Not implemented");
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				String line = new String(b, off, len);
				Platform.runLater(() -> logController.appendText(line));
			}
		};
	}
}
