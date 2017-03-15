package de.thk.rdw.admin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FooterController {

	@FXML
	private Label organization;
	@FXML
	private Label version;

	public void setOrganizationText(String value) {
		organization.setText(value);
	}

	public void setVersionText(String value) {
		version.setText(value);
	}
}
