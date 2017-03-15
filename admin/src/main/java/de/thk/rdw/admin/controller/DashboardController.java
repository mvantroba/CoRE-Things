package de.thk.rdw.admin.controller;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.model.TreeItemResource;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

public class DashboardController {

	private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private TreeView<TreeItemResource> resourceTree;

	@FXML
	private void initialize() {
	}

	public void populateTree(Response response) {
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false, true));
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}
