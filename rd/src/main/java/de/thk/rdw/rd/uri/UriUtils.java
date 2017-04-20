package de.thk.rdw.rd.uri;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UriUtils {

	private static final Logger LOGGER = Logger.getLogger(UriUtils.class.getName());

	public static Map<UriVariable, String> parseUriQuery(List<String> uriQuery) {
		Map<UriVariable, String> result = new EnumMap<>(UriVariable.class);
		for (String uriVariable : uriQuery) {
			String[] splittedVariable = uriVariable.split("=");
			try {
				if (splittedVariable.length != 2) {
					throw new IllegalArgumentException(
							String.format("Ignoring corrupted uri variable \"%s\".", uriVariable));
				}
				if (result.containsKey(UriVariable.getByName(splittedVariable[0]))) {
					throw new IllegalArgumentException(
							String.format("Ignoring duplicate uri variable \"%s\".", uriVariable));
				}
				UriVariable variable = UriVariable.getByName(splittedVariable[0]);
				if (variable == null) {
					throw new IllegalArgumentException(
							String.format("Ignoring invalid uri variable \"%s\".", uriVariable));
				}
				// Throws IllegalArgumentException.
				variable.validate(splittedVariable[1]);
				result.put(variable, splittedVariable[1]);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		return result;
	}
}
