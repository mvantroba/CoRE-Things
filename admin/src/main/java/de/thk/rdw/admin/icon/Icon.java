package de.thk.rdw.admin.icon;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Icon {

	CHECK_CIRCLE_GREEN("ic_check_circle_green"), //
	ENDPOINT_GREY("ic_endpoint_grey"), //
	ERROR_RED("ic_error_red"), //
	FOLDER_AMBER("ic_folder_amber"), //
	GROUP_WORK_GREY("ic_group_work_grey"), //
	HOME_GREEN("ic_home_green"), //
	INFO_BLUE("ic_info_blue"), //
	OPENHAB("ic_openhab"), //
	PAGEVIEW_GREY("ic_pageview_grey"), //
	PUBLIC_BLUE("ic_public_blue"), //
	RASPBERRY("ic_raspberry"), //
	RESOURCE_GREY("ic_resource_grey");

	private static final Logger LOGGER = Logger.getLogger(Icon.class.getName());
	private static final String DIRECTORY = "img";

	private String name;
	private Map<IconSize, Image> images = new EnumMap<>(IconSize.class);

	private Icon(String name) {
		this.name = name;
	}

	public Image getImage(IconSize iconSize) {
		Image result = null;
		String url = String.format("/%s/%s%s", DIRECTORY, name, iconSize.getExtension());
		if (images.containsKey(iconSize)) {
			result = images.get(iconSize);
		} else {
			try {
				result = new Image(url);
				images.put(iconSize, result);
				LOGGER.log(Level.FINE, "Loaded icon \"{0}\".", new Object[] { url });
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.INFO, "Could not load icon \"{0}\".", new Object[] { url });
			}
		}
		return result;
	}

	public ImageView getImageView(IconSize iconSize) {
		ImageView result = null;
		Image image = getImage(iconSize);
		if (image != null) {
			result = new ImageView(image);
		}
		return result;
	}
}