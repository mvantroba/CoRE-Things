package de.thk.rdw.endpoint.server.osgi;

public class DeviceServiceNotInitializedException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeviceServiceNotInitializedException() {
		super("Device service is not initialized.");
	}
}
