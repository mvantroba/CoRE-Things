package de.thk.ct.admin.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Request;

public class ExtendedCoapClient extends CoapClient {

	public ExtendedCoapClient() {
		super();
	}

	public void discover(MessageObserver observer) {
		Request request = Request.newGet().setURI(getURI());
		request.getOptions().clearUriPath().clearUriQuery().setUriPath("/.well-known/core");
		request.addMessageObserver(observer);
		request.send();
	}

	public void ping(MessageObserver observer) {
		Request request = Request.newPost().setURI(getURI());
		request.addMessageObserver(observer);
		send(request);
	}

	public void get(MessageObserver observer) {
		Request request = Request.newGet().setURI(getURI());
		request.addMessageObserver(observer);
		send(request);
	}

	public void post(MessageObserver observer, String payload, int format) {
		Request request = Request.newPost().setURI(getURI()).setPayload(payload);
		request.getOptions().setContentFormat(format);
		request.addMessageObserver(observer);
		send(request);
	}

	public void put(MessageObserver observer, String payload, int format) {
		Request request = Request.newPut().setURI(getURI()).setPayload(payload);
		request.getOptions().setContentFormat(format);
		request.addMessageObserver(observer);
		send(request);
	}

	public void delete(MessageObserver observer) {
		Request request = Request.newDelete().setURI(getURI());
		request.addMessageObserver(observer);
		send(request);
	}
}
