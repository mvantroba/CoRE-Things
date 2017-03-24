package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.AdvancedTreeCell;
import de.thk.rdw.admin.tree.TreeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class AdvancedController {

	private static final Logger LOGGER = Logger.getLogger(AdvancedController.class.getName());

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private TreeView<GuiCoapResource> resourceTree;
	@FXML
	private Label request;
	@FXML
	private TextArea requestPayload;
	@FXML
	private Label response;
	@FXML
	private TextArea responsePayload;

	private CoapClient coapClient;

	@FXML
	private void initialize() {
	}

	@FXML
	private void onActionGET() {
		coapClient = new CoapClient.Builder(mainController.getHost(), mainController.getPort())
				.path(mainController.getPath()).query(mainController.getQuery()).create();
		AdvancedController.this.request.setText(String.format("GET %s", coapClient.getURI()));
		coapClient.get(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						responsePayload.setText(response.getResponseText());
						AdvancedController.this.response
								.setText(String.format("Response Code: %s", response.getCode().toString()));
					}
				});
			}

			@Override
			public void onError() {
				// TODO Notification.
			}
		});
	}

	@FXML
	private void onActionPOST() {
		coapClient = new CoapClient.Builder(mainController.getHost(), mainController.getPort())
				.path(mainController.getPath()).query(mainController.getQuery()).create();
		AdvancedController.this.request.setText(String.format("POST %s", coapClient.getURI()));
		coapClient.post(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						responsePayload.setText(response.getResponseText());
						AdvancedController.this.response
								.setText(String.format("Response Code: %s", response.getCode().toString()));
					}
				});
			}

			@Override
			public void onError() {
				// TODO Notification.
			}
		}, requestPayload.getText(), MediaTypeRegistry.TEXT_PLAIN);
	}

	public void populateTree(Response response) {
		resourceTree.setCellFactory(new Callback<TreeView<GuiCoapResource>, TreeCell<GuiCoapResource>>() {

			@Override
			public TreeCell<GuiCoapResource> call(TreeView<GuiCoapResource> param) {
				return new AdvancedTreeCell();
			}
		});
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false));
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
