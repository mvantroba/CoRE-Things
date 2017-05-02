package de.thk.rdw.endpoint.server.osgi.network;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import de.thk.rdw.base.RdResourceType;

public class EndpointClient extends CoapClient {

	private static final Logger LOGGER = Logger.getLogger(EndpointClient.class.getName());

	private String rdUri;
	private String registrationQuery;

	public EndpointClient() {
		rdUri = buildRdUri();
		registrationQuery = buildRegistrationQuery();
	}

	public void sendRegistrationRequest(String payload) {
		final String uri = String.format("%s/%s?%s", rdUri, RdResourceType.CORE_RD.getName(), registrationQuery);
		Request request = Request.newPost().setURI(uri).setPayload(payload);
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		request.addMessageObserver(new EndpointClientObserver("REGISTRATION", uri));
		LOGGER.log(Level.INFO, "Sending REGISTRATION request to {0}...", new Object[] { uri });
		send(request);
	}

	public void sendRemovalRequest() {
		final String uri = String.format("%s/%s/%s", rdUri, RdResourceType.CORE_RD.getName(),
				EndpointConfig.getInstance().getString(EndpointConfig.Keys.ENDPOINT_NAME));
		Request request = Request.newDelete().setURI(uri);
		request.addMessageObserver(new EndpointClientObserver("DELETE", uri));
		LOGGER.log(Level.INFO, "Sending DELETE request to {0}...", new Object[] { uri });
		send(request);
	}

	private String buildRdUri() {
		StringBuilder result = new StringBuilder();
		EndpointConfig config = EndpointConfig.getInstance();

		result.append(config.getString(EndpointConfig.Keys.RD_SCHEME));
		result.append("://");
		result.append(config.getString(EndpointConfig.Keys.RD_HOST));
		result.append(":");
		result.append(config.getInt(EndpointConfig.Keys.RD_PORT));

		return result.toString();
	}

	private String buildRegistrationQuery() {
		StringBuilder result = new StringBuilder();
		EndpointConfig config = EndpointConfig.getInstance();

		result.append(LinkFormat.END_POINT).append("=").append(config.getString(EndpointConfig.Keys.ENDPOINT_NAME));
		result.append("&");
		result.append(LinkFormat.DOMAIN).append("=").append(config.getString(EndpointConfig.Keys.ENDPOINT_DOMAIN));
		result.append("&");
		result.append(LinkFormat.END_POINT_TYPE).append("=")
				.append(config.getString(EndpointConfig.Keys.ENDPOINT_TYPE));
		result.append("&");
		result.append(LinkFormat.LIFE_TIME).append("=").append(config.getString(EndpointConfig.Keys.ENDPOINT_LIFETIME));
		result.append("&");
		result.append(LinkFormat.CONTEXT).append("=").append(config.getString(EndpointConfig.Keys.ENDPOINT_CONTEXT));

		return result.toString();
	}

	private class EndpointClientObserver implements MessageObserver {

		private String requestType = "undefined";
		private String uri = "undefined";
		private int retransmission = 0;

		public EndpointClientObserver(String requestType, String uri) {
			this.requestType = requestType;
			this.uri = uri;
		}

		@Override
		public void onTimeout() {
			LOGGER.log(Level.SEVERE, "{0} request {1} timed out.", new Object[] { requestType, uri });
		}

		@Override
		public void onRetransmission() {
			retransmission++;
			LOGGER.log(Level.WARNING, "Retransmitting {0} request {1} ({2})...",
					new Object[] { requestType, uri, retransmission });
		}

		@Override
		public void onResponse(Response response) {
			LOGGER.log(Level.INFO, "Received response to {0} request from {1}:{2}. Code: {3}.", new Object[] {
					requestType, response.getSource(), response.getSourcePort(), response.getCode().toString() });
		}

		@Override
		public void onReject() {
			LOGGER.log(Level.SEVERE, "{0} request {1} has been rejected.", new Object[] { requestType, uri });
		}

		@Override
		public void onCancel() {
			LOGGER.log(Level.WARNING, "{0} request {1} has been canceled.", new Object[] { requestType, uri });
		}

		@Override
		public void onAcknowledgement() {
			LOGGER.log(Level.INFO, "{0} request {1} has been acknowledged.", new Object[] { requestType, uri });
		}
	}
}
