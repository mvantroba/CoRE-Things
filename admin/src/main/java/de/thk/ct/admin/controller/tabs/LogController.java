package de.thk.ct.admin.controller.tabs;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LogController {

	@FXML
	private TextArea log;

	@FXML
	private void onActionClear() {
		log.clear();
	}

	public void appendText(String line) {
		log.appendText(line);
	}
}
