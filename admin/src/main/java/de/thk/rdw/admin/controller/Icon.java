package de.thk.rdw.admin.controller;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Icon {

	HOME_GREEN_24("ic_home_green_24x24.png"), PUBLIC_BLUE_24("ic_public_blue_24x24.png"), FOLDER_AMBER_24(
			"ic_folder_amber_24x24.png"), GROUP_WORK_GREY_24("ic_group_work_grey_24x24.png"), PAGEVIEW_GREY_24(
					"ic_pageview_grey_24x24.png"), RASPBERRY_24("ic_raspberry_24x24.png"), OPENHAB_24(
							"ic_openhab_24x24.png"), ENDPOINT_GREY_24("ic_endpoint_grey_24x24.png"), INFO_BLUE_36(
									"ic_info_blue_36x36.png"), CHECK_CIRCLE_GREEN_36(
											"ic_check_circle_green_36x36.png"), ERROR_RED_36("ic_error_red_36x36.png");

	private static final String DIRECTORY = "img";

	private String fileName;
	private ImageView imageView;

	private Icon(String fileName) {
		this.fileName = fileName;
	}

	public ImageView getImageView() {
		if (imageView == null) {
			InputStream inputStream = getClass().getResourceAsStream(getPath());
			if (inputStream != null) {
				imageView = new ImageView(new Image(inputStream));
			}
		}
		return imageView;
	}

	private String getPath() {
		return String.format("/%s/%s", DIRECTORY, fileName);
	}
}
