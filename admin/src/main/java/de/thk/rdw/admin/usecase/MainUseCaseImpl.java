package de.thk.rdw.admin.usecase;

import java.util.List;

import javax.persistence.EntityManager;

import de.thk.rdw.admin.data.CoapConnectionDao;
import de.thk.rdw.admin.data.CoapConnectionDaoImpl;
import de.thk.rdw.admin.data.PersistenceManager;
import de.thk.rdw.admin.model.CoapConnection;
import de.thk.rdw.admin.service.CoapConnectionService;
import de.thk.rdw.admin.service.CoapConnectionServiceImpl;

public class MainUseCaseImpl implements MainUseCase {

	private EntityManager entityManager;
	private CoapConnectionDao coapConnectionDao;
	private CoapConnectionService coapConnectionService;

	public MainUseCaseImpl() {
		entityManager = PersistenceManager.getInstance().geEntityManager();
		coapConnectionDao = new CoapConnectionDaoImpl(entityManager);
		coapConnectionService = new CoapConnectionServiceImpl(coapConnectionDao);
	}

	@Override
	public List<CoapConnection> findAllConnections() {
		return coapConnectionService.findAll();
	}

	@Override
	public void cleanUp() {
		PersistenceManager.getInstance().close();
	}
}
