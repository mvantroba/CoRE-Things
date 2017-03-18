package de.thk.rdw.admin.controller.tabs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class PlaceholderPanelController implements Initializable {

	@FXML
	private Label title;
	@FXML
	private Label description;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	public void setTitle(String value) {
		title.setText(value);
	}

	public void setDescription(String value) {
		description.setText(value);
	}
}
