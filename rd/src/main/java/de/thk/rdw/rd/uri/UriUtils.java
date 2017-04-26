package de.thk.rdw.rd.uri;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.network.Exchange;

/**
 * Helper class which can be used to parse and validate URI variables from a
 * CoAP request. Variables are represented by {@link UriVariable} which defines
 * available variables that can be used in this application.
 * 
 * @author Martin Vantroba
 *
 */
public class UriUtils {

	private static final Logger LOGGER = Logger.getLogger(UriUtils.class.getName());

	private UriUtils() {
	}

	/**
	 * Can be used to parse {@link List} of URI variables from a CoAP request.
	 * The method {@link OptionSet #getUriQuery()} can be used to obtain such
	 * {@link List} from an {@link Exchange}.
	 * <p>
	 * This method returns a {@link Map} which includes all variables that are
	 * defined in {@link UriVariable}, that are not duplicates and have valid
	 * values. All other variables won't be returned by this method.
	 * 
	 * @param uriQuery
	 *            list of URI variables
	 * @return valid URI variables
	 */
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
