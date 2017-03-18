package de.thk.rdw.admin.controller.tabs;

import java.net.URL;
import java.util.ResourceBundle;

import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class EndpointPanelController implements Initializable {

	@FXML
	private Label endpointName;
	@FXML
	private Label domain;
	@FXML
	private Label endpointType;
	@FXML
	private Label lifetime;
	@FXML
	private Label context;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	public void setGuiCoapResource(GuiCoapResource guiCoapResource) {
		endpointName.setText(guiCoapResource.getName());
		// TODO Define attribute names globally.
		domain.setText(guiCoapResource.getAttributeValues("d"));
		endpointType.setText(guiCoapResource.getAttributeValues("et"));
		lifetime.setText(guiCoapResource.getAttributeValues("lt"));
		context.setText(guiCoapResource.getAttributeValues("con"));
	}
}
