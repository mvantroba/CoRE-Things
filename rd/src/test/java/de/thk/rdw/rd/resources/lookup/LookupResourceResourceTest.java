package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.rdw.base.RdLookupType;
import de.thk.rdw.base.RdResourceType;
import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdLookupResource;
import de.thk.rdw.rd.resources.RdResource;

public class LookupResourceResourceTest {

	private CoapServer server;
	private String lookupResourceUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		lookupResourceUri = String.format("coap://localhost:%d/%s/%s", endpoint.getAddress().getPort(),
				RdResourceType.CORE_RD_LOOKUP.getName(), RdLookupType.RESOURCE);
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_NoEndpoints_Expect_NotFound() {
		String uri = lookupResourceUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_EmptyEndpoint_Expect_NotFound() {
		String uri = lookupResourceUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", null));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_MultipleEndpoints_Expect_Content() {
		String uri = lookupResourceUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		// Endpoint 1
		EndpointResource endpointResource1 = new EndpointResource("node1", "domain1");
		endpointResource1.add(new CoapResource("temperature"));

		// Endpoint 2
		EndpointResource endpointResource2 = new EndpointResource("node2", "domain2");
		Resource actuators = new CoapResource("actuators");
		actuators.add(new CoapResource("led"));
		actuators.add(new CoapResource("servo"));
		endpointResource2.add(actuators);

		// Endpoint 3
		EndpointResource endpointResource3 = new EndpointResource("node3", "domain3");

		// RD
		RdResource rdResource = new RdResource();
		rdResource.add(endpointResource1, endpointResource2, endpointResource3);

		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(4, client.get().getResponseText().split(",").length);
	}
}
