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

import de.thk.ct.rd.resources.GroupResource;
import de.thk.ct.rd.resources.RdGroupResource;
import de.thk.ct.rd.resources.RdLookupResource;
import de.thk.ct.rd.resources.RdResource;
import de.thk.ct.rd.resources.RdResourceType;

public class LookupGroupResourceTest {

	private CoapServer server;
	private String lookupGroupUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		lookupGroupUri = String.format("coap://localhost:%d/%s/%s", endpoint.getAddress().getPort(),
				RdResourceType.CORE_RD_LOOKUP.getName(), RdLookupType.GROUP);
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_NoGroups_Expect_NotFound() {
		String uri = lookupGroupUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		server.add(new RdLookupResource(rdResource, rdGroupResource));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_WrongType_Expect_NotFound() {
		String uri = lookupGroupUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		rdGroupResource.add(new CoapResource("group1"));
		server.add(new RdLookupResource(rdResource, rdGroupResource));

		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_TwoGroups_Expect_TwoFound() {
		String uri = lookupGroupUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		rdGroupResource.add(new GroupResource("group1", null));
		rdGroupResource.add(new GroupResource("group2", null));
		server.add(new RdLookupResource(rdResource, rdGroupResource));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}
}
