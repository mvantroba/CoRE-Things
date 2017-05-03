package de.thk.ct.endpoint.server.osgi.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EndpointConfig {

	private static final Logger LOGGER = Logger.getLogger(EndpointConfig.class.getName());
	private static final String FILENAME = "Endpoint.properties";
	private static final String FILE_HEADER = "Endpoint Properties file";

	private static EndpointConfig instance;

	private Properties properties;

	public class Keys {
		public static final String RD_SCHEME = "RD_SCHEME";
		public static final String RD_PORT = "RD_PORT";
		public static final String RD_HOST = "RD_HOST";
		public static final String ENDPOINT_NAME = "ENDPOINT_NAME";
		public static final String ENDPOINT_DOMAIN = "ENDPOINT_DOMAIN";
		public static final String ENDPOINT_TYPE = "ENDPOINT_TYPE";
		public static final String ENDPOINT_LIFETIME = "ENDPOINT_LIFETIME";
		public static final String ENDPOINT_CONTEXT = "ENDPOINT_CONTEXT";
	}

	private EndpointConfig() {
		this.properties = new Properties();
		EndpointConfigDefaults.setDefaults(this);
	}

	public static EndpointConfig getInstance() {
		if (instance == null) {
			synchronized (EndpointConfig.class) {
				if (instance == null)
					createInstance();
			}
		}
		return instance;
	}

	private static EndpointConfig createInstance() {
		File file = new File(FILENAME);
		instance = new EndpointConfig();
		if (file.exists()) {
			LOGGER.info("Loading properties from file " + file);
			try {
				instance.load(file);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error while loading properties from " + file.getAbsolutePath(), e);
			}
		} else {
			LOGGER.info("Storing properties in file " + file);
			try {
				instance.store(file);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error while storing properties to " + file.getAbsolutePath(), e);
			}
		}
		return instance;
	}

	private void load(File file) throws IOException {
		InputStream inStream = new FileInputStream(file);
		properties.load(inStream);
	}

	private void store(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException();
		}
		properties.store(new FileWriter(file), FILE_HEADER);
	}

	public String getString(String key) {
		return properties.getProperty(key);
	}

	public int getInt(String key) {
		String value = properties.getProperty(key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING,
						"Could not convert property \"" + key + "\" with value \"" + value + "\" to integer", e);
			}
		} else {
			LOGGER.warning("Property \"" + key + "\" is undefined");
		}
		return 0;
	}

	public long getLong(String key) {
		String value = properties.getProperty(key);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING,
						"Could not convert property \"" + key + "\" with value \"" + value + "\" to long", e);
				return 0;
			}
		} else {
			LOGGER.warning("Property \"" + key + "\" is undefined");
		}
		return 0;
	}

	public EndpointConfig setString(String key, String value) {
		properties.put(key, String.valueOf(value));
		return this;
	}

	public EndpointConfig setInt(String key, int value) {
		properties.put(key, String.valueOf(value));
		return this;
	}

	public EndpointConfig setLong(String key, long value) {
		properties.put(key, String.valueOf(value));
		return this;
	}
}
