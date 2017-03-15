package de.thk.rdw.admin;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(AdminApplication.class.getName());

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/bundle");
		Scene scene = new Scene(loadMainLayout(bundle));
		primaryStage.setTitle(bundle.getString("project.name"));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Parent loadMainLayout(ResourceBundle bundle) {
		Parent result = null;
		FXMLLoader loader;
		try {
			loader = new FXMLLoader();
			loader.setLocation(AdminApplication.class.getResource("/fxml/Main.fxml"));
			loader.setResources(bundle);
			result = loader.load();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}
}
