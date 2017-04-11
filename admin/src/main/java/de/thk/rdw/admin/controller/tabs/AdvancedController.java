package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.AdvancedTreeCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class AdvancedController {

	private MainController mainController;

	@FXML
	private ResourceBundle resources;
	@FXML
	private TreeView<GuiCoapResource> resourceTree;
	@FXML
	private Label request;
	@FXML
	private TextArea requestPayload;
	@FXML
	private Label response;
	@FXML
	private TextArea responsePayload;

	public void populateTree(TreeItem<GuiCoapResource> rootItem) {
		this.resourceTree.setCellFactory(param -> new AdvancedTreeCell());
		this.resourceTree.setRoot(rootItem);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@FXML
	private void onActionGET() {
		mainController.coapGET();
	}

	@FXML
	private void onActionPOST() {
		mainController.coapPOST();
	}
}
