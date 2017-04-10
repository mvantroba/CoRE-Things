package de.thk.rdw.admin.usecase;

import java.util.List;

import org.eclipse.californium.core.coap.MessageObserver;

import de.thk.rdw.admin.model.CoapConnection;

public interface MainUseCase {

	void createCoapConnection(CoapConnection coapConnection);

	List<CoapConnection> findAllCoapConnections(boolean createDefault);

	CoapConnection findCoapConnectionById(CoapConnection coapConnection);

	void updateCoapConnection(CoapConnection coapConnection);

	void deleteCoapConnection(CoapConnection coapConnection);

	void setCoapURI(String uri);

	void coapDiscover(String uri, MessageObserver observer);

	void coapPing(String uri, MessageObserver observer);

	void coapGET(String uri, MessageObserver observer);

	void coapPOST(String uri, String payload, MessageObserver observer, int format);

	void cleanUp();
}
