package de.thk.ct.admin.controller.tabs;

import java.util.ResourceBundle;

import de.thk.ct.admin.controller.MainController;
import de.thk.ct.admin.model.GuiCoapResource;
import de.thk.ct.admin.tree.AdvancedTreeCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	@FXML
	private TextField query;
	@FXML
	private TextField path;

	public void populateTree(TreeItem<GuiCoapResource> rootItem) {
		this.resourceTree.setCellFactory(param -> new AdvancedTreeCell());
		this.resourceTree.setRoot(rootItem);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public String getPath() {
		return path.getText();
	}

	public String getQuery() {
		return query.getText();
	}

	public void setRequest(String request) {
		this.request.setText(request);
	}

	public void setResponse(String response) {
		this.response.setText(response);
	}

	public String getRequestPayload() {
		return requestPayload.getText();
	}

	public void setResponsePayload(String text) {
		responsePayload.setText(text);
	}

	@FXML
	private void onActionGET() {
		mainController.coapGET();
	}

	@FXML
	private void onActionPOST() {
		mainController.coapPOST();
	}

	@FXML
	private void onActionPUT() {
		mainController.coapPUT();
	}

	@FXML
	private void onActionDELETE() {
		mainController.coapDELETE();
	}
}
