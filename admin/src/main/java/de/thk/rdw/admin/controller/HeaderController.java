package de.thk.rdw.admin.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class HeaderController implements Initializable {

	@FXML
	private Label title;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		title.setText(getImplementationTitle().toUpperCase());
	}

	private String getImplementationTitle() {
		// Read from manifest file.
		String result = getClass().getPackage().getImplementationTitle();
		if (result == null) {
			result = "implementationTitle";
		}
		return result;
	}
}
