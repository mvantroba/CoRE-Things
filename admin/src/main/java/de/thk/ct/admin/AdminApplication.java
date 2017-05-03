package de.thk.ct.admin;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.ct.admin.controller.MainController;
import de.thk.ct.admin.usecase.MainUseCase;
import de.thk.ct.admin.usecase.MainUseCaseImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(AdminApplication.class.getName());

	private MainUseCase mainUseCase;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create a facade which provides access to all services and DAOs for
		// the main controller.
		mainUseCase = new MainUseCaseImpl();

		ResourceBundle bundle = ResourceBundle.getBundle("i18n/bundle");
		Scene scene = new Scene(loadMainLayout(mainUseCase, bundle, primaryStage));
		primaryStage.setTitle(getImplementationTitle());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		mainUseCase.cleanUp();
	}

	private Parent loadMainLayout(MainUseCase mainUseCase, ResourceBundle bundle, Stage primaryStage) {
		Parent result = null;
		FXMLLoader loader;
		try {
			loader = new FXMLLoader();
			loader.setLocation(AdminApplication.class.getResource("/fxml/Main.fxml"));
			loader.setResources(bundle);
			result = loader.load();
			MainController mainController = loader.getController();
			mainController.setUseCase(mainUseCase);
			mainController.setPrimaryStage(primaryStage);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
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
