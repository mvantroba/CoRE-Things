package de.thk.rdw.endpoint.pi4j.osgi.resources;

public abstract class AbstractPi4jResource {

	protected String name;

	public AbstractPi4jResource(String name) {
		this.name = name;
	}

	public abstract void init();

	public abstract void destroy();

	public abstract String getType();

	public abstract String getValue();
}
