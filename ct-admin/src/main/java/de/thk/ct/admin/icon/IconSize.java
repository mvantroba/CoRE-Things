package de.thk.ct.admin.icon;

public enum IconSize {

	SMALL("_16px.png"), MEDIUM("_32px.png"), LARGE("_48px.png");

	private String extension;

	private IconSize(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}
}
