package de.thk.rdw.admin.controller;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Icon {

	HOME_GREEN_24("ic_home_green_24x24.png"), INFO_BLUE_36("ic_info_blue_36x36.png"), CHECK_CIRCLE_GREEN_36(
			"ic_check_circle_green_36x36.png"), ERROR_RED_36("ic_error_red_36x36.png");

	private static final String DIRECTORY = "img";

	private String fileName;

	private Icon(String fileName) {
		this.fileName = fileName;
	}

	public ImageView getImageView() {
		ImageView result = null;
		InputStream inputStream = getClass().getResourceAsStream(getPath());
		if (inputStream != null) {
			result = new ImageView(new Image(inputStream));
		}
		return result;
	}

	private String getPath() {
		return String.format("/%s/%s", DIRECTORY, fileName);
	}
}
