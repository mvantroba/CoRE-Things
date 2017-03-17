package de.thk.rdw.admin.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Icon {

	CHECK_CIRCLE_GREEN_36("ic_check_circle_green_36px.png"), //
	ENDPOINT_GREY_16("ic_endpoint_grey_16px.png"), //
	ERROR_RED_36("ic_error_red_36px.png"), //
	FOLDER_AMBER_16("ic_folder_amber_16px.png"), //
	GROUP_WORK_GREY_16("ic_group_work_grey_16px.png"), //
	HOME_GREEN_16("ic_home_green_16px.png"), //
	INFO_BLUE_36("ic_info_blue_36px.png"), //
	OPENHAB_16("ic_openhab_16px.png"), //
	PAGEVIEW_GREY_16("ic_pageview_grey_16px.png"), //
	PUBLIC_BLUE_16("ic_public_blue_16px.png"), //
	RASPBERRY_16("ic_raspberry_16px.png"), //
	RESOURCE_GREY_16("ic_resource_grey_16px.png");

	private static final Logger LOGGER = Logger.getLogger(Icon.class.getName());
	private static final String DIRECTORY = "img";

	private String fileName;
	private Image image;

	private Icon(String fileName) {
		this.fileName = fileName;
	}

	public ImageView getImageView() {
		ImageView result = null;
		String url = String.format("/%s/%s", DIRECTORY, fileName);
		if (image == null) {
			try {
				image = new Image(url);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.INFO, "Could not load icon \"{0}\".", new Object[] { url });
			}
		}
		if (image != null) {
			result = new ImageView(image);
		}
		return result;
	}
}
