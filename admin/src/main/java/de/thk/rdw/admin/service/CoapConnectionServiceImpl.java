package de.thk.rdw.admin.service;

import java.util.List;

import org.eclipse.californium.core.coap.CoAP;

import de.thk.rdw.admin.data.CoapConnectionDao;
import de.thk.rdw.admin.model.CoapConnection;

public class CoapConnectionServiceImpl implements CoapConnectionService {

	private CoapConnectionDao coapConnectionDao;

	public CoapConnectionServiceImpl(CoapConnectionDao coapConnectionDao) {
		this.coapConnectionDao = coapConnectionDao;
	}

	@Override
	public List<CoapConnection> findAll() {
		List<CoapConnection> coapConnections = coapConnectionDao.findAll();
		if (coapConnections.isEmpty()) {
			createLocalhost();
			coapConnections = coapConnectionDao.findAll();
		}
		return coapConnections;
	}

	private void createLocalhost() {
		CoapConnection coapConnection = new CoapConnection("Localhost", CoAP.COAP_URI_SCHEME, "127.0.0.1",
				CoAP.DEFAULT_COAP_PORT);
		coapConnectionDao.beginTransaction();
		coapConnectionDao.create(coapConnection);
		coapConnectionDao.commitTransaction();
	}
}
