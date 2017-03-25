package de.thk.rdw.admin.data;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {

	private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());
	private static final String PERSISTENCE_UNIT_NAME = "rdw-admin";

	private static PersistenceManager instance;

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	private PersistenceManager() {
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		LOGGER.log(Level.INFO, "{0} with persistence unit name \"{1}\" has been initialized.",
				new Object[] { EntityManagerFactory.class.getSimpleName(), PERSISTENCE_UNIT_NAME });
	}

	public static PersistenceManager getInstance() {
		if (instance == null) {
			instance = new PersistenceManager();
		}
		return instance;
	}

	public EntityManager geEntityManager() {
		if (entityManager == null) {
			entityManager = entityManagerFactory.createEntityManager();
			LOGGER.log(Level.INFO, "{0} has been created.", new Object[] { EntityManager.class.getSimpleName() });
		}
		return entityManager;
	}

	public void close() {
		entityManager.close();
		LOGGER.log(Level.INFO, "{0} has been closed.", new Object[] { EntityManager.class.getSimpleName() });
		entityManagerFactory.close();
		LOGGER.log(Level.INFO, "{0} has been closed.", new Object[] { EntityManagerFactory.class.getSimpleName() });
	}
}
