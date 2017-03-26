package de.thk.rdw.admin.usecase;

import java.util.List;

import org.eclipse.californium.core.coap.MessageObserver;

import de.thk.rdw.admin.model.CoapConnection;

public interface MainUseCase {

	List<CoapConnection> findAllCoapConnections();

	void setCoapURI(String uri);

	void coapDiscover(String uri, MessageObserver observer);

	void coapGET(String uri, MessageObserver observer);

	void coapPOST(String uri, String payload, MessageObserver observer, int format);

	void cleanUp();
}
