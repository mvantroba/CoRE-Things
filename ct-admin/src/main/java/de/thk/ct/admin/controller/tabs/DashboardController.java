package de.thk.ct.admin.controller.tabs;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.ct.admin.controller.MainController;
import de.thk.ct.admin.model.GuiCoapResource;
import de.thk.ct.admin.tree.DashboardTreeCell;
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
	}

	public void resetPanels() {
		onClosePanelA();
		onClosePanelB();
	}

	private void initPlaceholderPanels() {
		placeholderPanelAController.setTitle(resources.getString("placeholder.panelA.title"));
		placeholderPanelAController.setDescription(resources.getString("placeholder.panelA.description"));
		placeholderPanelBController.setTitle(resources.getString("placeholder.panelB.title"));
		placeholderPanelBController.setDescription(resources.getString("placeholder.panelB.description"));
	}

	private void initEndpointPanelA() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ENDPOINT_PANEL_FXML));
			loader.setResources(resources);
			endpointPanelA = loader.load();
			endpointPanelAController = loader.getController();
			endpointPanelAController.setMainController(mainController);
			endpointPanelAController.setOnCloseEventHandler(event -> onClosePanelA());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void initEndpointPanelB() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ENDPOINT_PANEL_FXML));
			loader.setResources(resources);
			endpointPanelB = loader.load();
			endpointPanelBController = loader.getController();
			endpointPanelBController.setMainController(mainController);
			endpointPanelBController.setOnCloseEventHandler(event -> onClosePanelB());
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

	private void onClosePanelA() {
		setPanelA(placeholderPanelA);
	}

	private void onClosePanelB() {
		setPanelB(placeholderPanelB);
	}

	public void populateTree(TreeItem<GuiCoapResource> rootItem) {
		resourceTree.setCellFactory(new Callback<TreeView<GuiCoapResource>, TreeCell<GuiCoapResource>>() {

			@Override
			public TreeCell<GuiCoapResource> call(TreeView<GuiCoapResource> param) {
				return new DashboardTreeCell(resources) {

					@Override
					protected void onShowOnPanelA(GuiCoapResource guiCoapResource) {
						initEndpointPanelA();
						endpointPanelA.setOpacity(0.0);
						endpointPanelAController.setGuiCoapResource(guiCoapResource);
						setPanelA(endpointPanelA);
						fadeIn.setNode(endpointPanelA);
						fadeIn.playFromStart();
					}

					@Override
					protected void onShowOnPanelB(GuiCoapResource guiCoapResource) {
						initEndpointPanelB();
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
