package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXML;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	@FXML
	private ResourceBundle resources;
	@FXML
	private HeaderController headerController;
	@FXML
	private NotificationController notificationController;
	@FXML
	private DashboardController dashboardController;

	@FXML
	private void initialize() {
		dashboardController.setMainController(this);
	}

	public void success(String message) {
		notificationController.success(message);
	}

	public void spinner(String message) {
		notificationController.spinner(message);
	}

}
