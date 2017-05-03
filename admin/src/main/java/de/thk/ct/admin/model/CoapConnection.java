package de.thk.ct.admin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class CoapConnection {

	private LongProperty id = new SimpleLongProperty();
	private StringProperty name = new SimpleStringProperty();
	private StringProperty scheme = new SimpleStringProperty();
	private StringProperty host = new SimpleStringProperty();
	private IntegerProperty port = new SimpleIntegerProperty();

	public CoapConnection() {
	}

	public CoapConnection(String name, String scheme, String host, int port) {
		this.name.set(name);
		this.scheme.set(scheme);
		this.host.set(host);
		this.port.set(port);
	}

	@Override
	public String toString() {
		return name.get();
	}

	// id

	@Id
	@GeneratedValue
	public long getId() {
		return id.get();
	}

	public void setId(long id) {
		this.id.set(id);
	}

	// name

	public StringProperty getNameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	// scheme

	public StringProperty getSchemeProperty() {
		return scheme;
	}

	public String getScheme() {
		return scheme.get();
	}

	public void setScheme(String scheme) {
		this.scheme.set(scheme);
	}

	// host

	public StringProperty getHostProperty() {
		return host;
	}

	public String getHost() {
		return host.get();
	}

	public void setHost(String host) {
		this.host.set(host);
	}

	// port

	public IntegerProperty getPortProperty() {
		return port;
	}

	public int getPort() {
		return port.get();
	}

	public void setPort(int port) {
		this.port.set(port);
	}
}
