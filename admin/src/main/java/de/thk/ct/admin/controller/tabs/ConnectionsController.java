package de.thk.ct.admin.controller.tabs;

import de.thk.ct.admin.controller.MainController;
import de.thk.ct.admin.model.CoapConnection;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ConnectionsController {

	private MainController mainController;

	@FXML
	private Button add;
	@FXML
	private Button edit;
	@FXML
	private Button delete;

	@FXML
	private TableView<CoapConnection> connections;
	@FXML
	private TableColumn<CoapConnection, String> name;
	@FXML
	private TableColumn<CoapConnection, String> scheme;
	@FXML
	private TableColumn<CoapConnection, String> host;
	@FXML
	private TableColumn<CoapConnection, Integer> port;

	@FXML
	private void initialize() {
		edit.disableProperty().bind(Bindings.isEmpty(connections.getSelectionModel().getSelectedItems()));
		delete.disableProperty().bind(Bindings.isEmpty(connections.getSelectionModel().getSelectedItems()));
		name.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
		scheme.setCellValueFactory(cellData -> cellData.getValue().getSchemeProperty());
		host.setCellValueFactory(cellData -> cellData.getValue().getHostProperty());
		port.setCellValueFactory(cellData -> cellData.getValue().getPortProperty().asObject());
	}

	@FXML
	private void onActionAdd() {
		CoapConnection coapConnection = new CoapConnection();
		if (mainController.showCoapConnectionDialog(coapConnection)) {
			mainController.saveCoapConnection(coapConnection);
		}
	}

	@FXML
	private void onActionEdit() {
		CoapConnection coapConnection = connections.getSelectionModel().getSelectedItem();
		if (mainController.showCoapConnectionDialog(coapConnection)) {
			mainController.saveCoapConnection(coapConnection);
		}
	}

	@FXML
	private void onActionDelete() {
		mainController.deleteCoapConnection(connections.getSelectionModel().getSelectedItem());
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public void setCoapConnections(ObservableList<CoapConnection> value) {
		connections.setItems(value);
	}
}
