package de.thk.rdw.admin.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FooterController implements Initializable {

	@FXML
	private Label vendor;
	@FXML
	private Label version;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vendor.setText(String.format("© %s", getImplementationVendor()));
		version.setText(getImplementationVersion());
	}

	private String getImplementationVendor() {
		// Read from manifest file.
		String result = getClass().getPackage().getImplementationVendor();
		if (result == null) {
			result = "implementationVendor";
		}
		return result;
	}

	private String getImplementationVersion() {
		// Read from manifest file.
		String result = getClass().getPackage().getImplementationVersion();
		if (result == null) {
			result = "implementationVersion";
		}
		return result;
	}
}
