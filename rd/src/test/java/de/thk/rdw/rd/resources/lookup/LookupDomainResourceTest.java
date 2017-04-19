package de.thk.rdw.rd.resources.lookup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.rdw.rd.resources.EndpointResource;
import de.thk.rdw.rd.resources.RdLookupResource;
import de.thk.rdw.rd.resources.RdResource;
import de.thk.rdw.rd.resources.ResourceType;

public class LookupDomainResourceTest {

	private static final long ENDPOINT_LIFETIME = 86400L;

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
				ResourceType.CORE_RD_LOOKUP.getName(), LookupType.DOMAIN);
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

		// Mock empty RD resource.
		Collection<Resource> children = new ArrayList<>();
		RdResource rdResource = mock(RdResource.class);
		when(rdResource.getChildren()).thenReturn(children);
		server.add(new RdLookupResource(rdResource, null));

		// Expect "not found".
		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_WrongResourceType_Expect_NotFound() {
		String uri = lookupDomainUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		// Mock RD resource with wrong resource type (CoapResource instead of
		// EndpointResource).
		Collection<Resource> children = new ArrayList<>();
		children.add(new CoapResource("node1"));
		RdResource rdResource = mock(RdResource.class);
		when(rdResource.getChildren()).thenReturn(children);
		server.add(new RdLookupResource(rdResource, null));

		// Expect "not found".
		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}

	@Test
	public void When_TwoEndpointPairs_Expect_ValidFormat() {
		String testedDomain1 = "domain1";
		String testedDomain2 = "domain2";
		String uri = lookupDomainUri;
		CoapClient client = new CoapClient(uri).useExecutor();

		// Mock RD resource with two endpoint pairs.
		Collection<Resource> children = new ArrayList<>();
		children.add(new EndpointResource("node1", testedDomain1, ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node2", testedDomain1, ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node3", testedDomain2, ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node4", testedDomain2, ENDPOINT_LIFETIME));
		RdResource rdResource = mock(RdResource.class);
		when(rdResource.getChildren()).thenReturn(children);
		server.add(new RdLookupResource(rdResource, null));

		// Expect format according to
		// https://tools.ietf.org/html/draft-ietf-core-resource-directory-08#section-8
		Assert.assertEquals(String.format("<>;%s=\"%s\",<>;%s=\"%s\"", LinkFormat.DOMAIN, testedDomain1,
				LinkFormat.DOMAIN, testedDomain2), client.get().getResponseText());
	}

	@Test
	public void When_TwoEndpointPairs_And_GoodDomainQuery_Expect_OneDomain() {
		String testedDomain = "domain1";
		String uri = String.format("%s?%s=%s", lookupDomainUri, LinkFormat.DOMAIN, testedDomain);
		CoapClient client = new CoapClient(uri).useExecutor();

		// Mock RD resource with two endpoint pairs.
		Collection<Resource> children = new ArrayList<>();
		children.add(new EndpointResource("node1", testedDomain, ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node2", testedDomain, ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node3", "domain2", ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node4", "domain2", ENDPOINT_LIFETIME));
		RdResource rdResource = mock(RdResource.class);
		when(rdResource.getChildren()).thenReturn(children);
		server.add(new RdLookupResource(rdResource, null));

		String responseText = client.get().getResponseText();
		// Expect one domain.
		Assert.assertEquals(1, responseText.split(",").length);
		Assert.assertTrue(responseText.contains(testedDomain));
	}

	@Test
	public void When_TwoEndpointPairs_And_BadDomainQuery_Expect_NotFound() {
		String uri = String.format("%s?%s=%s", lookupDomainUri, LinkFormat.DOMAIN, "domain3");
		CoapClient client = new CoapClient(uri).useExecutor();

		// Mock RD resource with two endpoint pairs.
		Collection<Resource> children = new ArrayList<>();
		children.add(new EndpointResource("node1", "domain1", ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node2", "domain1", ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node3", "domain2", ENDPOINT_LIFETIME));
		children.add(new EndpointResource("node4", "domain2", ENDPOINT_LIFETIME));
		RdResource rdResource = mock(RdResource.class);
		when(rdResource.getChildren()).thenReturn(children);
		server.add(new RdLookupResource(rdResource, null));

		// Expect "not found".
		Assert.assertEquals(ResponseCode.NOT_FOUND, client.get().getCode());
	}
}
