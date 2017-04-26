package de.thk.rdw.rd;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of this module. It initializes and starts {@link RdServer}
 * which can be used as a repository for web links hosted on other web servers.
 * 
 * @author Martin Vantroba
 *
 */
public class RdApplication {

	private static final Logger LOGGER = Logger.getLogger(RdApplication.class.getName());

	private RdApplication() {
	}

	public static void main(String[] args) {
		LOGGER.log(Level.INFO, "Starting Resource Directory...");
		RdServer rdServer = new RdServer();
		rdServer.start();
	}
}
