package de.thk.rdw.rd.resources.lookup;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.GroupResource;
import de.thk.rdw.rd.resources.RdGroupResource;
import de.thk.rdw.rd.resources.RdLookupResource;
import de.thk.rdw.rd.resources.RdResource;
import de.thk.rdw.rd.resources.ResourceType;
import de.thk.rdw.rd.uri.UriVariable;

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
	public void When_TwoEndpoints_Expect_TwoFound() {
		String uri = lookupEndpointUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", null));
		rdResource.add(new EndpointResource("node2", null));
		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}

	@Test
	public void When_TwoGroups_Expect_TwoEndpointsFound() {
		String testedGroup = "group1";
		String uri = String.format("%s?%s=%s", lookupEndpointUri, UriVariable.GROUP.getName(), testedGroup);
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", null));
		rdResource.add(new EndpointResource("node2", null));
		rdResource.add(new EndpointResource("node3", null));
		rdResource.add(new EndpointResource("node4", null));

		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		GroupResource group1Resource = new GroupResource(testedGroup, null);
		group1Resource.add(new EndpointResource("node1", null));
		group1Resource.add(new EndpointResource("node2", null));
		GroupResource group2Resource = new GroupResource("group2", null);
		group2Resource.add(new EndpointResource("node3", null));
		group2Resource.add(new EndpointResource("node4", null));

		rdGroupResource.add(group1Resource);
		rdGroupResource.add(group2Resource);

		server.add(new RdLookupResource(rdResource, rdGroupResource));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}

	@Test
	public void When_TwoDomains_Expect_TwoEndpointsFound() {
		String testedDomain = "domain1";
		String uri = String.format("%s?%s=%s", lookupEndpointUri, LinkFormat.DOMAIN, testedDomain);
		CoapClient client = new CoapClient(uri).useExecutor();

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", testedDomain));
		rdResource.add(new EndpointResource("node2", testedDomain));
		rdResource.add(new EndpointResource("node3", "domain2"));
		rdResource.add(new EndpointResource("node4", "domain2"));

		server.add(new RdLookupResource(rdResource, null));

		Assert.assertEquals(2, client.get().getResponseText().split(",").length);
	}
}
