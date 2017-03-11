package de.thk.rdw.admin.controller;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Icon {

	HOME_GREEN_LARGE("ic_home_green_96x96.png");

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

	public ImageView getImageView(double width, double height) {
		ImageView result = getImageView();
		if (result != null) {
			result.setFitWidth(width);
			result.setFitHeight(height);
		}
		return result;
	}

	public ImageView getImageView(double size) {
		return getImageView(size, size);
	}

	private String getPath() {
		return String.format("/%s/%s", DIRECTORY, fileName);
	}
}
