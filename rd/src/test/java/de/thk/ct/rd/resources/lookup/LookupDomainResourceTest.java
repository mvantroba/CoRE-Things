package de.thk.ct.rd.resources.lookup;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.ct.base.RdLookupType;
import de.thk.ct.base.RdResourceType;
import de.thk.ct.rd.resources.EndpointResource;
import de.thk.ct.rd.resources.RdLookupResource;
import de.thk.ct.rd.resources.RdResource;

public class LookupDomainResourceTest {

	private CoapServer server;
	private String lookupDomainUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		lookupDomainUri = String.format("coap://localhost:%d/%s/%s", endpoint.getAddress().getPort(),
				RdResourceType.CORE_RD_LOOKUP.getName(), RdLookupType.DOMAIN);
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_NoEndpoints_Expect_NotFound() {
		String uri = lookupDomainUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		server.add(new RdLookupResource(new RdResource(), null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_WrongResourceType_Expect_NotFound() {
		String uri = lookupDomainUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new CoapResource("node1"));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_TwoEndpointPairs_Expect_TwoDomains() {
		String testedDomain1 = "domain1";
		String testedDomain2 = "domain2";
		String uri = lookupDomainUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", testedDomain1));
		rdResource.add(new EndpointResource("node2", testedDomain1));
		rdResource.add(new EndpointResource("node3", testedDomain2));
		rdResource.add(new EndpointResource("node4", testedDomain2));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}
}
