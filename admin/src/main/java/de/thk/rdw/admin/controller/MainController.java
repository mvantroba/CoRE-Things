package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import javafx.fxml.FXML;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	@FXML
	private ResourceBundle resources;
	@FXML
	private FooterController footerController;
	@FXML
	private HeaderController headerController;
	@FXML
	private TargetController targetController;
	@FXML
	private NotificationController notificationController;
	@FXML
	private DashboardController dashboardController;

	@FXML
	private void initialize() {
		dashboardController.setMainController(this);
		targetController.setMainController(this);
	}

	public void populateTree(Response response) {
		dashboardController.populateTree(response);
	}

	public void success(String key) {
		notificationController.success(resources.getString(key));
	}

	public void spinner(String key) {
		notificationController.spinner(resources.getString(key));
	}
}
