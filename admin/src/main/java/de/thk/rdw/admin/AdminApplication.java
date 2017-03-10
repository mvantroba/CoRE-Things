package de.thk.rdw.admin;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(AdminApplication.class.getName());

	private static final String BUNDLE_BASE_NAME = "i18n/bundle";

	private Stage primaryStage;
	private BorderPane rootLayout;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Resource Directory Admin");
		initRootLayout();
		showManage();
	}

	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AdminApplication.class.getResource("/fxml/Root.fxml"));
			loader.setResources(ResourceBundle.getBundle(BUNDLE_BASE_NAME));
			rootLayout = loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void showManage() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AdminApplication.class.getResource("/fxml/Manage.fxml"));
			loader.setResources(ResourceBundle.getBundle(BUNDLE_BASE_NAME));
			VBox resourceDirectory = loader.load();
			rootLayout.setCenter(resourceDirectory);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
