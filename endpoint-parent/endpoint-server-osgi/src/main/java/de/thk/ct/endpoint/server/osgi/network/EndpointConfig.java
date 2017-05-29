package de.thk.ct.endpoint.server.osgi.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration for {@link EndpointClient} which reads configuration from a
 * property file.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointConfig {

	private static final Logger LOGGER = Logger.getLogger(EndpointConfig.class.getName());
	private static final String FILENAME = "Endpoint.properties";
	private static final String FILE_HEADER = "Endpoint Properties file";

	private static EndpointConfig instance;

	private Properties properties;

	/**
	 * List of defined keys which are used to store the endpoint configuration
	 * into a property file.
	 * 
	 * @author Martin Vantroba
	 *
	 */
	public class Keys {

		private Keys() {
		}

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

	/**
	 * Lazy loads the singleton instance.
	 * 
	 * @return singleton instance of this class
	 */
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
			LOGGER.log(Level.INFO, "Loading properties from file {0}...", new Object[] { file.getAbsolutePath() });
			try {
				instance.load(file);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error while loading properties from {0}. {1}",
						new Object[] { file.getAbsolutePath(), e.getMessage() });
			}
		} else {
			LOGGER.log(Level.INFO, "Storing properties in file {0}...", new Object[] { file.getAbsolutePath() });
			try {
				instance.store(file);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error while storing properties to {0}. {1}",
						new Object[] { file.getAbsolutePath(), e.getMessage() });
			}
		}
		return instance;
	}

	private void load(File file) throws IOException {
		properties.load(new FileInputStream(file));
	}

	private void store(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException();
		}
		properties.store(new FileWriter(file), FILE_HEADER);
	}

	/**
	 * Reads configuration property by the given key as a string.
	 * 
	 * @param key
	 *            property key
	 * @return property value
	 */
	public String getString(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Reads configuration property by the given key as an int.
	 * 
	 * @param key
	 *            property key
	 * @return property value
	 */
	public int getInt(String key) {
		String value = properties.getProperty(key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Could not convert property \"{0}\" with value \"{1}\" to integer.",
						new Object[] { key, value });
			}
		} else {
			LOGGER.log(Level.WARNING, "Property \"{0}\" is undefined.", new Object[] { key });
		}
		return 0;
	}

	/**
	 * Reads configuration property by the given key as a long.
	 * 
	 * @param key
	 *            property key
	 * @return property value
	 */
	public long getLong(String key) {
		String value = properties.getProperty(key);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Could not convert property \"{0}\" with value \"{1}\" to long.",
						new Object[] { key, value });
			}
		} else {
			LOGGER.log(Level.WARNING, "Property \"{0}\" is undefined.", new Object[] { key });
		}
		return 0;
	}

	/**
	 * Updates the value of the string property by the given key.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            property value
	 * @return updated configuration
	 */
	public EndpointConfig setString(String key, String value) {
		properties.put(key, String.valueOf(value));
		return this;
	}

	/**
	 * Updates the value of the int property by the given key.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            property value
	 * @return updated configuration
	 */
	public EndpointConfig setInt(String key, int value) {
		properties.put(key, String.valueOf(value));
		return this;
	}

	/**
	 * Updates the value of the long property by the given key.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            property value
	 * @return updated configuration
	 */
	public EndpointConfig setLong(String key, long value) {
		properties.put(key, String.valueOf(value));
		return this;
	}
}
