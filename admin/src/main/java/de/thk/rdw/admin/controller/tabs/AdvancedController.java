package de.thk.rdw.admin.controller.tabs;

import java.util.ResourceBundle;

import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.controller.MainController;
import de.thk.rdw.admin.model.GuiCoapResource;
import de.thk.rdw.admin.tree.AdvancedTreeCell;
import de.thk.rdw.admin.tree.TreeUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

	public void populateTree(Response response) {
		this.resourceTree.setCellFactory(param -> new AdvancedTreeCell());
		this.resourceTree.setRoot(TreeUtils.parseResources(response, false));
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
