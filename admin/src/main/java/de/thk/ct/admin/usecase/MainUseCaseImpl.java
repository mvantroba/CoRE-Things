package de.thk.ct.admin.usecase;

import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MessageObserver;

import de.thk.ct.admin.coap.ExtendedCoapClient;
import de.thk.ct.admin.data.CoapConnectionDao;
import de.thk.ct.admin.data.CoapConnectionDaoImpl;
import de.thk.ct.admin.data.PersistenceManager;
import de.thk.ct.admin.model.CoapConnection;

public class MainUseCaseImpl implements MainUseCase {

	private CoapConnectionDao coapConnectionDao;
	private ExtendedCoapClient extendedCoapClient;

	public MainUseCaseImpl() {
		EntityManager entityManager = PersistenceManager.getInstance().geEntityManager();
		coapConnectionDao = new CoapConnectionDaoImpl(entityManager);
		extendedCoapClient = new ExtendedCoapClient();
	}

	@Override
	public void createCoapConnection(CoapConnection coapConnection) {
		coapConnectionDao.beginTransaction();
		coapConnectionDao.create(coapConnection);
		coapConnectionDao.commitTransaction();
	}

	@Override
	public List<CoapConnection> findAllCoapConnections(boolean createDefault) {
		List<CoapConnection> coapConnections = coapConnectionDao.findAll();
		if (coapConnections.isEmpty() && createDefault) {
			createDefaultCoapConnection();
			coapConnections = coapConnectionDao.findAll();
		}
		return coapConnections;
	}

	@Override
	public CoapConnection findCoapConnectionById(CoapConnection coapConnection) {
		return coapConnectionDao.findById(coapConnection);
	}

	@Override
	public void updateCoapConnection(CoapConnection coapConnection) {
		coapConnectionDao.beginTransaction();
		coapConnectionDao.update(coapConnection);
		coapConnectionDao.commitTransaction();
	}

	@Override
	public void deleteCoapConnection(CoapConnection coapConnection) {
		coapConnectionDao.beginTransaction();
		coapConnectionDao.delete(coapConnection);
		coapConnectionDao.commitTransaction();
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
	public void coapObserve(String uri, CoapHandler coapHandler) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.observe(coapHandler);
	}

	@Override
	public void coapPing(String uri, MessageObserver observer) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.ping(observer);
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
	public void coapPUT(String uri, String payload, MessageObserver observer, int format) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.put(observer, payload, format);
	}

	@Override
	public void coapDELETE(String uri, MessageObserver observer) {
		extendedCoapClient.setURI(uri);
		extendedCoapClient.delete(observer);
	}

	@Override
	public void cleanUp() {
		PersistenceManager.getInstance().close();
		extendedCoapClient.shutdown();
	}

	private void createDefaultCoapConnection() {
		CoapConnection coapConnection = new CoapConnection("Localhost", CoAP.COAP_URI_SCHEME, "127.0.0.1",
				CoAP.DEFAULT_COAP_PORT);
		coapConnectionDao.beginTransaction();
		coapConnectionDao.create(coapConnection);
		coapConnectionDao.commitTransaction();
	}

}
