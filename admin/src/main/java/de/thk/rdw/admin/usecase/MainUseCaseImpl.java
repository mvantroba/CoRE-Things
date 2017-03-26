package de.thk.rdw.admin.usecase;

import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.californium.core.coap.MessageObserver;

import de.thk.rdw.admin.coap.ExtendedCoapClient;
import de.thk.rdw.admin.data.CoapConnectionDao;
import de.thk.rdw.admin.data.CoapConnectionDaoImpl;
import de.thk.rdw.admin.data.PersistenceManager;
import de.thk.rdw.admin.model.CoapConnection;
import de.thk.rdw.admin.service.CoapConnectionService;
import de.thk.rdw.admin.service.CoapConnectionServiceImpl;

public class MainUseCaseImpl implements MainUseCase {

	private CoapConnectionDao coapConnectionDao;
	private CoapConnectionService coapConnectionService;
	private ExtendedCoapClient extendedCoapClient;

	public MainUseCaseImpl() {
		EntityManager entityManager = PersistenceManager.getInstance().geEntityManager();
		coapConnectionDao = new CoapConnectionDaoImpl(entityManager);
		coapConnectionService = new CoapConnectionServiceImpl(coapConnectionDao);
		extendedCoapClient = new ExtendedCoapClient();
	}

	@Override
	public List<CoapConnection> findAllCoapConnections() {
		return coapConnectionService.findAll();
	}

	@Override
	public void setCoapURI(String uri) {
		extendedCoapClient.setURI(uri);
	}

	@Override
	public void coapDiscover(String uri, MessageObserver observer) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.discover(observer);
	}

	@Override
	public void coapGET(String uri, MessageObserver observer) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.get(observer);
	}

	@Override
	public void coapPOST(String uri, String payload, MessageObserver observer, int format) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.post(observer, payload, format);
	}

	@Override
	public void cleanUp() {
		PersistenceManager.getInstance().close();
		extendedCoapClient.shutdown();
	}
}
