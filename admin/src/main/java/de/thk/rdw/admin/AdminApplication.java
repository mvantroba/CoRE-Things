package de.thk.rdw.admin;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(AdminApplication.class.getName());

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Resource Directory Admin");
		Button button = new Button();
		button.setText("Test Logger");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				LOGGER.log(Level.INFO, "This is \"{0}\".", new Object[] { AdminApplication.class.getSimpleName() });
			}
		});
		StackPane rootPane = new StackPane();
		rootPane.getChildren().add(button);
		primaryStage.setScene(new Scene(rootPane, 320, 180));
		primaryStage.show();
	}
}
