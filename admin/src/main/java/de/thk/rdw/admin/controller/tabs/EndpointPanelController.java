package de.thk.rdw.admin.controller.tabs;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.admin.icon.EndpointTypeIcon;
import de.thk.rdw.admin.icon.Icon;
import de.thk.rdw.admin.icon.IconSize;
import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class EndpointPanelController implements Initializable {

	private static final Logger LOGGER = Logger.getLogger(EndpointPanelController.class.getName());

	@FXML
	private Label endpointName;
	@FXML
	private ImageView endpointImage;
	@FXML
	private Label domain;
	@FXML
	private Label endpointType;
	@FXML
	private Label lifetime;
	@FXML
	private Label context;
	@FXML
	private Button close;
	@FXML
	private ListView<GuiCoapResource> endpointResources;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		endpointResources.setCellFactory(param -> new EndpointResourceCell() {

			@Override
			public void onToggle(GuiCoapResource item) {
				// TODO Use CoapClient class to send requests.
				Request request = new Request(CoAP.Code.POST);
				String uri = String.format("%s%s", item.getEndpointUri(), item.getRelativePath());
				request.setURI(uri);
				request.addMessageObserver(new MessageObserverAdapter() {

					@Override
					public void onResponse(Response response) {
						LOGGER.log(Level.INFO, "Received response code: \"{0}\".", response.getCode());
					}
				});
				LOGGER.log(Level.INFO, "Sending POST request to \"{0}\"...", uri);
				request.send();
			}
		});
	}

	public void setOnCloseEventHandler(EventHandler<ActionEvent> eventHandler) {
		close.setOnAction(eventHandler);
	}

	public void setGuiCoapResource(GuiCoapResource guiCoapResource) {
		endpointName.setText(guiCoapResource.getName());
		Icon icon = EndpointTypeIcon.get(guiCoapResource.getAttributes().getAttributeValues("et"));
		endpointImage.setImage(icon.getImage(IconSize.LARGE));
		// TODO Define attribute names globally.
		domain.setText(guiCoapResource.getAttributeValues("d"));
		endpointType.setText(guiCoapResource.getAttributeValues("et"));
		lifetime.setText(guiCoapResource.getAttributeValues("lt"));
		context.setText(guiCoapResource.getAttributeValues("con"));
		ObservableList<GuiCoapResource> items = FXCollections.observableArrayList(guiCoapResource.getLeafNodes());
		endpointResources.getItems().clear();
		endpointResources.setItems(items);
	}
}
