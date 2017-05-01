package de.thk.rdw.endpoint.pi4j.osgi.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.rdw.base.ActuatorType;
import de.thk.rdw.base.ResourceType;
import de.thk.rdw.base.SensorType;

/**
 * Helper class which can be used to read sensor and actuator lists from a CSV
 * files. It returns already validated arguments that can be used to initialize
 * sensors and actuators.
 * 
 * @author Martin Vantroba
 *
 */
public class CsvReader {

	private static final Logger LOGGER = Logger.getLogger(CsvReader.class.getName());

	private static final String SENSORS_FILENAME = "EndpointSensors.csv";
	private static final String ACTUATORS_FILENAME = "EndpointActuators.csv";

	private static final String SEPARATOR = ",";

	private CsvReader() {
	}

	/**
	 * Reads either sensors or actuators from a CSV file and validates them.
	 * 
	 * @param resourceType
	 *            defines if sensors or actuators will be obtained
	 * @return validated list of arguments that can be used for initialization
	 *         of sensors or actuators
	 */
	public static List<String[]> readResources(ResourceType resourceType) {
		List<String[]> result;
		if (resourceType.equals(ResourceType.SENSOR)) {
			result = read(new File(SENSORS_FILENAME), ResourceType.SENSOR);
		} else {
			result = read(new File(ACTUATORS_FILENAME), ResourceType.ACTUATOR);
		}
		return result;
	}

	private static List<String[]> read(File file, ResourceType resourceType) {
		List<String[]> result = new ArrayList<>();
		try {
			// File will be created only if it does not exist.
			if (file.createNewFile()) {
				LOGGER.log(Level.INFO, "File \"{0}\" was not found, new one has been created.",
						new Object[] { file.getAbsolutePath() });
			} else {
				LOGGER.log(Level.INFO, "Reading data from \"{0}\".", new Object[] { file.getAbsolutePath() });
				result.addAll(getValidLines(file, resourceType));
				if (result.isEmpty()) {
					LOGGER.log(Level.INFO, "No valid lines were found in file \"{0}\".",
							new Object[] { file.getAbsolutePath() });
				}
			}
		} catch (IOException | SecurityException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	private static List<String[]> getValidLines(File file, ResourceType resourceType) {
		List<String[]> result = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					// This line is a comment.
					continue;
				}
				String[] arguments = line.split(SEPARATOR);
				if (validate(arguments, resourceType)) {
					result.add(arguments);
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	/**
	 * Validates list of arguments that should contain at least resource name,
	 * resource type and one additional parameter.
	 * 
	 * @param arguments
	 *            sensor or actuator arguments
	 * @param resourceType
	 *            sensor or actuator
	 * @return true if arguments are valid
	 */
	public static boolean validate(String[] arguments, ResourceType resourceType) {
		boolean result = true;
		try {
			if (arguments == null) {
				throw new IllegalArgumentException("Line is null.");
			}
			if (arguments.length < 3) {
				throw new IllegalArgumentException("Not enough arguments.");
			}
			if (arguments[0].trim().isEmpty()) {
				throw new IllegalArgumentException("Name cannot be empty.");
			}
			if (arguments[1].trim().isEmpty()) {
				throw new IllegalArgumentException("Type cannot be empty.");
			}
			switch (resourceType) {
			case SENSOR:
				if (!SensorType.containsType(arguments[1])) {
					throw new IllegalArgumentException(
							String.format("Sensor type \"%s\" does not exists.", arguments[1]));
				}
				break;
			case ACTUATOR:
				if (!ActuatorType.containsType(arguments[1])) {
					throw new IllegalArgumentException(
							String.format("Actuator type \"%s\" does not exists.", arguments[1]));
				}
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not read line \"{0}\". {1}",
					new Object[] { Arrays.toString(arguments), e.getMessage() });
			result = false;
		}
		return result;
	}
}
