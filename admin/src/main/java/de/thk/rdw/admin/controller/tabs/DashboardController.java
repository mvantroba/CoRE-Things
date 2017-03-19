package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.DashboardTreeCell;
import de.thk.rdw.admin.tree.TreeUtils;
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
	private PlaceholderPanelController placeholderPanelAController;
	@FXML
	private PlaceholderPanelController placeholderPanelBController;

	@FXML
	private void initialize() {
		placeholderPanelAController.setTitle(resources.getString("placeholder.panelA.title"));
		placeholderPanelAController.setDescription(resources.getString("placeholder.panelA.description"));
		placeholderPanelBController.setTitle(resources.getString("placeholder.panelB.title"));
		placeholderPanelBController.setDescription(resources.getString("placeholder.panelB.description"));
	}

	public void populateTree(Response response) {
		resourceTree.setCellFactory(new Callback<TreeView<GuiCoapResource>, TreeCell<GuiCoapResource>>() {

			@Override
			public TreeCell<GuiCoapResource> call(TreeView<GuiCoapResource> param) {
				return new DashboardTreeCell(resources) {

					@Override
					protected void onShowOnPanelA(GuiCoapResource guiCoapResource) {
						endpointPanelController.setGuiCoapResource(guiCoapResource);
					}

					@Override
					protected void onShowOnPanelB(GuiCoapResource guiCoapResource) {
						// TODO Auto-generated method stub
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
