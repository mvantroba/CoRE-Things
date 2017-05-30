package de.thk.ct.admin.controller.tabs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlaceholderPanelController {

	@FXML
	private Label title;
	@FXML
	private Label description;

	public void setTitle(String value) {
		title.setText(value);
	}

	public void setDescription(String value) {
		description.setText(value);
	}
}
