package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.CoapResourceCell;
import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.controller.TreeUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class DashboardController {

	private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private TreeView<CoapResource> resourceTree;

	@FXML
	private void initialize() {
	}

	public void populateTree(Response response) {
		resourceTree.setCellFactory(new Callback<TreeView<CoapResource>, TreeCell<CoapResource>>() {

			@Override
			public TreeCell<CoapResource> call(TreeView<CoapResource> param) {
				return new CoapResourceCell();
			}
		});
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false, true));
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}
