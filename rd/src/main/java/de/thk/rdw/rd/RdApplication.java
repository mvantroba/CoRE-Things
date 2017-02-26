package de.thk.rdw.rd;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RdApplication {

	private static final Logger LOGGER = Logger.getLogger(RdApplication.class.getName());

	public static void main(String[] args) {
		LOGGER.log(Level.INFO, "Starting Resource Directory...");
		RdServer rdServer = new RdServer();
		rdServer.start();
	}
}
