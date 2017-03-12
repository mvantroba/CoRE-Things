package de.thk.rdw.admin.controller;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.rdw.admin.AdminApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	@FXML
	private ResourceBundle resources;
	@FXML
	private Tab dashboard;

	@FXML
	private void initialize() {
		String path = String.format("%s/%s", AdminApplication.FXML_BASE_FOLDER, "Dashboard.fxml");
		Node dashboardLayout;
		FXMLLoader loader;
		try {
			loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource(path));
			loader.setResources(resources);
			dashboardLayout = loader.load();
			dashboard.setContent(dashboardLayout);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
