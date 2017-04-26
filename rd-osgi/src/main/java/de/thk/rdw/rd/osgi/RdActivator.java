package de.thk.rdw.rd.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.thk.rdw.rd.RdServer;

/**
 * The {@link BundleActivator} of this bundle. It initializes and starts
 * {@link RdServer} which can be used as a repository for web links hosted on
 * other web servers.
 * 
 * @author Martin Vantroba
 *
 */
public class RdActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(RdActivator.class.getName());

	private RdServer rdServer;

	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Resource Directory\"...");
		rdServer = new RdServer();
		rdServer.start();
		LOGGER.log(Level.INFO, "Bundle \"RDW Resource Directory\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Resource Directory\"...");
		rdServer.stop();
		LOGGER.log(Level.INFO, "Bundle \"RDW Resource Directory\" is stopped.");
	}
}
