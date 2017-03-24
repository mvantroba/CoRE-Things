package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.tabs.AdvancedController;
import de.thk.rdw.admin.controller.tabs.DashboardController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
	private static final Number ADVANCED_TAB_INDEX = 1;

	@FXML
	private ResourceBundle resources;
	@FXML
	private TabPane tabPane;
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
		targetController.setMainController(this);
		dashboardController.setMainController(this);
		advancedController.setMainController(this);
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue == ADVANCED_TAB_INDEX) {
					targetController.disableAdvanced(false);
				} else {
					targetController.disableAdvanced(true);
				}
			}
		});
	}

	public void populateTree(Response response) {
		dashboardController.populateTree(response);
		advancedController.populateTree(response);
	}

	public void success(String key) {
		notificationController.success(resources.getString(key));
	}

	public void spinner(String key) {
		notificationController.spinner(resources.getString(key));
	}

	public String getHost() {
		return targetController.getHost();
	}

	public int getPort() {
		return targetController.getPort();
	}

	public String getPath() {
		return targetController.getPath();
	}

	public String getQuery() {
		return targetController.getQuery();
	}
}
