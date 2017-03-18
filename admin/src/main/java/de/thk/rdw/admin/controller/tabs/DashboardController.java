package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.CoapResourceCell;
import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.controller.TreeUtils;
import de.thk.rdw.admin.model.GuiCoapResource;
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
	private TreeView<GuiCoapResource> resourceTree;
	@FXML
	private EndpointPanelController endpointPanelController;

	@FXML
	private void initialize() {
	}

	public void populateTree(Response response) {
		resourceTree.setCellFactory(new Callback<TreeView<GuiCoapResource>, TreeCell<GuiCoapResource>>() {

			@Override
			public TreeCell<GuiCoapResource> call(TreeView<GuiCoapResource> param) {
				return new CoapResourceCell() {

					@Override
					protected void onShowAction(GuiCoapResource guiCoapResource) {
						endpointPanelController.setGuiCoapResource(guiCoapResource);
					}
				};
			}
		});
		this.resourceTree.setRoot(TreeUtils.parseResources(response, true));
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}
