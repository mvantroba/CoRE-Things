package de.thk.rdw.admin.controller.tabs;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.DashboardTreeCell;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

public class DashboardController {

	private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
	// TODO Define fxml directories globaly.
	private static final String ENDPOINT_PANEL_FXML = "/fxml/tabs/EndpointPanel.fxml";
	private static final int PANEL_A_INDEX = 2;
	private static final int PANEL_B_INDEX = 4;

	private MainController mainController;

	@FXML
	private HBox root;
	@FXML
	private ResourceBundle resources;
	@FXML
	private TreeView<GuiCoapResource> resourceTree;
	@FXML
	private VBox placeholderPanelA;
	@FXML
	private PlaceholderPanelController placeholderPanelAController;
	@FXML
	private VBox placeholderPanelB;
	@FXML
	private PlaceholderPanelController placeholderPanelBController;

	private VBox endpointPanelA;
	private EndpointPanelController endpointPanelAController;
	private VBox endpointPanelB;
	private EndpointPanelController endpointPanelBController;

	private FadeTransition fadeIn;

	@FXML
	private void initialize() {
		fadeIn = new FadeTransition(Duration.millis(250.0));
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		initPlaceholderPanels();
		initEndpointPanels();
	}

	private void initPlaceholderPanels() {
		placeholderPanelAController.setTitle(resources.getString("placeholder.panelA.title"));
		placeholderPanelAController.setDescription(resources.getString("placeholder.panelA.description"));
		placeholderPanelBController.setTitle(resources.getString("placeholder.panelB.title"));
		placeholderPanelBController.setDescription(resources.getString("placeholder.panelB.description"));
	}

	private void initEndpointPanels() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ENDPOINT_PANEL_FXML));
			loader.setResources(resources);
			endpointPanelA = loader.load();
			endpointPanelAController = loader.getController();
			endpointPanelAController.setOnCloseEventHandler(event -> setPanelA(placeholderPanelA));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ENDPOINT_PANEL_FXML));
			loader.setResources(resources);
			endpointPanelB = loader.load();
			endpointPanelBController = loader.getController();
			endpointPanelBController.setOnCloseEventHandler(event -> setPanelB(placeholderPanelB));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void setPanelA(VBox panel) {
		root.getChildren().set(PANEL_A_INDEX, panel);
	}

	private void setPanelB(VBox panel) {
		root.getChildren().set(PANEL_B_INDEX, panel);
	}

	public void populateTree(TreeItem<GuiCoapResource> rootItem) {
		resourceTree.setCellFactory(new Callback<TreeView<GuiCoapResource>, TreeCell<GuiCoapResource>>() {

			@Override
			public TreeCell<GuiCoapResource> call(TreeView<GuiCoapResource> param) {
				return new DashboardTreeCell(resources) {

					@Override
					protected void onShowOnPanelA(GuiCoapResource guiCoapResource) {
						endpointPanelA.setOpacity(0.0);
						endpointPanelAController.setGuiCoapResource(guiCoapResource);
						setPanelA(endpointPanelA);
						fadeIn.setNode(endpointPanelA);
						fadeIn.playFromStart();
					}

					@Override
					protected void onShowOnPanelB(GuiCoapResource guiCoapResource) {
						endpointPanelB.setOpacity(0.0);
						endpointPanelBController.setGuiCoapResource(guiCoapResource);
						setPanelB(endpointPanelB);
						fadeIn.setNode(endpointPanelB);
						fadeIn.playFromStart();
					}
				};
			}
		});
		this.resourceTree.setRoot(rootItem);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}
