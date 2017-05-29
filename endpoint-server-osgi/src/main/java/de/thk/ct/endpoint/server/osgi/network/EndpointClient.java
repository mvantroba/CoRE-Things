package de.thk.ct.endpoint.server.osgi.network;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

/**
 * {@link CoapClient} which sends continuous registration, update and removal
 * requests to a resource directory. Registration URI and query are obtained
 * from the {@link EndpointConfig}. Registration payload which contains
 * resources can be updated via the {@link #setRegistrationPayload(String)}
 * method.
 * 
 * @author Martin Vantroba
 *
 */
public class EndpointClient extends CoapClient {

	private static final Logger LOGGER = Logger.getLogger(EndpointClient.class.getName());
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	private String rdUri;
	private String registrationQuery;
	private String registrationPayload;
	private ScheduledFuture<?> scheduledFuture;

	/**
	 * Constructs an {@link EndpointClient} and initializes registration URI and
	 * query which are obtained from the {@link EndpointConfig}.
	 */
	public EndpointClient() {
		rdUri = buildRdUri();
		registrationQuery = buildRegistrationQuery();
	}

	/**
	 * Starts sending registration requests to the defined URI while using
	 * defined query and payload. The period of the registration requests is
	 * based on the "lifetime" parameter which is obtained from the
	 * {@link EndpointConfig}.
	 */
	public void startRegistration() {
		// Contingency time.
		long period = EndpointConfig.getInstance().getLong(EndpointConfig.Keys.ENDPOINT_LIFETIME) - 2;
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		scheduledFuture = SCHEDULER.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				final String uri = String.format("%s/%s?%s", rdUri, RdResourceType.CORE_RD.getName(),
						registrationQuery);
				Request request = Request.newPost().setURI(uri).setPayload(registrationPayload);
				request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
				request.addMessageObserver(new EndpointClientObserver("REGISTRATION", uri));
				LOGGER.log(Level.INFO, "Sending REGISTRATION request to {0}...", new Object[] { uri });
				send(request);
			}
		}, 0L, period, TimeUnit.SECONDS);
	}

	/**
	 * Cancels the thread which was started by the {@link #startRegistration()}
	 * method and sends a registration removal request to the resource
	 * directory.
	 */
	public void resetRegistration() {
		if (scheduledFuture != null) {
			// Shutdown thread so it wont send registration requests anymore.
			scheduledFuture.cancel(true);
		}
		final String uri = String.format("%s/%s/%s", rdUri, RdResourceType.CORE_RD.getName(),
				EndpointConfig.getInstance().getString(EndpointConfig.Keys.ENDPOINT_NAME));
		Request request = Request.newDelete().setURI(uri);
		request.addMessageObserver(new EndpointClientObserver("DELETE", uri));
		LOGGER.log(Level.INFO, "Sending DELETE request to {0}...", new Object[] { uri });
		send(request);
	}

	public void setRegistrationPayload(String registrationPayload) {
		this.registrationPayload = registrationPayload;
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

		private String requestType = "[]";
		private String uri = "[]";
		private int retransmission = 0;

		public EndpointClientObserver(String requestType, String uri) {
			this.requestType = requestType;
			this.uri = uri;
		}

		@Override
		public void onTimeout() {
			LOGGER.log(Level.WARNING, "{0} request to {1} timed out.", new Object[] { requestType, uri });
		}

		@Override
		public void onRetransmission() {
			retransmission++;
			LOGGER.log(Level.WARNING, "Retransmitting {0} request to {1} ({2})...",
					new Object[] { requestType, uri, retransmission });
		}

		@Override
		public void onResponse(Response response) {
			LOGGER.log(Level.INFO, "Received response to {0} request from {1}:{2}. Code: {3}.", new Object[] {
					requestType, response.getSource(), response.getSourcePort(), response.getCode().toString() });
		}

		@Override
		public void onReject() {
			LOGGER.log(Level.WARNING, "{0} request to {1} has been rejected.", new Object[] { requestType, uri });
		}

		@Override
		public void onCancel() {
			LOGGER.log(Level.WARNING, "{0} request to {1} has been canceled.", new Object[] { requestType, uri });
		}

		@Override
		public void onAcknowledgement() {
			LOGGER.log(Level.INFO, "{0} request to {1} has been acknowledged.", new Object[] { requestType, uri });
		}
	}
}
