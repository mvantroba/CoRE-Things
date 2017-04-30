package de.thk.rdw.endpoint.pi4j.osgi.resources;

public abstract class AbstractDeviceResource {

	protected String name;

	private DeviceResourceListener listener;

	public AbstractDeviceResource(String name, DeviceResourceListener listener) {
		this.name = name;
		this.listener = listener;
	}

	public abstract void init();

	public abstract void destroy();

	public abstract String getType();

	public abstract String getValue();

	public void onChanged(String value) {
		listener.onChanged(value);
	}

	public String getName() {
		return name;
	}
}
