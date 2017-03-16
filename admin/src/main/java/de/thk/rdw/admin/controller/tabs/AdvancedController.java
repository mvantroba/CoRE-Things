package de.thk.rdw.admin.controller.tabs;

import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.controller.TreeUtils;
import de.thk.rdw.admin.model.TreeItemResource;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

public class AdvancedController {

	private static final Logger LOGGER = Logger.getLogger(AdvancedController.class.getName());

	private MainController mainController;

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