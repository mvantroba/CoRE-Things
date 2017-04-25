package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdLookupResource;
import de.thk.rdw.rd.resources.RdResource;
import de.thk.rdw.rd.resources.ResourceType;

public class LookupEndpointResourceTest {

	private CoapServer server;
	private String lookupEndpointUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		lookupEndpointUri = String.format("coap://localhost:%d/%s/%s", endpoint.getAddress().getPort(),
				ResourceType.CORE_RD_LOOKUP.getName(), LookupType.ENDPOINT);
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_NoEndpoints_Expect_NotFound() {
		String uri = lookupEndpointUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_WrongType_Expect_NotFound() {
		String uri = lookupEndpointUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new CoapResource("node1"));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_TwoEndpoints_Expect_TwoFound() {
		String uri = lookupEndpointUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", "domain1"));
		rdResource.add(new EndpointResource("node2", "domain2"));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}
}
