package de.thk.rdw.rd.resources;

import java.util.ArrayList;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.thk.rdw.rd.uri.UriVariable;

public class RdGroupResourceTest {

	private CoapServer server;
	private String rdGroupUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		rdGroupUri = String.format("%s://localhost:%d/%s", CoAP.COAP_URI_SCHEME, endpoint.getAddress().getPort(),
				ResourceType.CORE_RD_GROUP.getName());
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_POST_NoGroupParameter_Expect_BadRequest() {
		server.add(new RdGroupResource(new RdResource()));

		CoapClient client = new CoapClient(rdGroupUri).useExecutor();
		ResponseCode responseCode = client.post(String.format(""), MediaTypeRegistry.TEXT_PLAIN).getCode();

		Assert.assertEquals(ResponseCode.BAD_REQUEST, responseCode);
	}

	@Test
	public void When_POST_EndpointNotRegisterd_Expect_NotFound() {
		String uri = String.format("%s?%s=group1", rdGroupUri, UriVariable.GROUP.getName());
		server.add(new RdGroupResource(new RdResource()));

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client
				.post(LinkFormat.serializeResource(new EndpointResource("node1", "domain1")).toString(),
						MediaTypeRegistry.TEXT_PLAIN)
				.getCode();

		Assert.assertEquals(ResponseCode.NOT_FOUND, responseCode);
	}

	@Test
	public void When_POST_EndpointInWrongDomain_Expect_NotFound() {
		String uri = String.format("%s?%s=group1&%s=domain1", rdGroupUri, UriVariable.GROUP.getName(),
				LinkFormat.DOMAIN);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", "domain1"));
		// Add one endpoint to default domain.
		rdResource.add(new EndpointResource("node2", null));
		server.add(new RdGroupResource(rdResource));

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(LinkFormat.serializeTree(rdResource), MediaTypeRegistry.TEXT_PLAIN)
				.getCode();

		Assert.assertEquals(ResponseCode.NOT_FOUND, responseCode);
	}

	@Test
	public void When_POST_EndpointsInGoodDomain_Expect_Created() {
		String testedGroup = "group1";
		String testedDomain = "domain1";
		String uri = String.format("%s?%s=%s&%s=%s", rdGroupUri, UriVariable.GROUP.getName(), testedGroup,
				LinkFormat.DOMAIN, testedDomain);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", testedDomain));
		rdResource.add(new EndpointResource("node2", testedDomain));
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(LinkFormat.serializeTree(rdResource), MediaTypeRegistry.TEXT_PLAIN)
				.getCode();

		Assert.assertEquals(ResponseCode.CREATED, responseCode);
		ArrayList<Resource> groups = new ArrayList<>(rdGroupResource.getChildren());
		Assert.assertEquals(1, groups.size());
		Resource group = groups.get(0);
		Assert.assertEquals(testedGroup, group.getName());
		Assert.assertEquals(2, group.getChildren().size());
	}

	@Test
	public void When_POST_GroupExists_Expect_Changed() {
		String testedGroup = "group1";
		String testedDomain = "domain1";
		String uri = String.format("%s?%s=%s&%s=%s", rdGroupUri, UriVariable.GROUP.getName(), testedGroup,
				LinkFormat.DOMAIN, testedDomain);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", testedDomain));
		rdResource.add(new EndpointResource("node2", testedDomain));
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);

		GroupResource groupResource = new GroupResource(testedGroup, testedDomain);
		groupResource.add(new EndpointResource("node1", testedDomain));
		rdGroupResource.add(groupResource);
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(LinkFormat.serializeTree(rdResource), MediaTypeRegistry.TEXT_PLAIN)
				.getCode();

		Assert.assertEquals(ResponseCode.CHANGED, responseCode);
		ArrayList<Resource> groups = new ArrayList<>(rdGroupResource.getChildren());
		Assert.assertEquals(1, groups.size());
		Resource group = groups.get(0);
		Assert.assertEquals(testedGroup, group.getName());
		Assert.assertEquals(2, group.getChildren().size());
	}

	@Test
	public void When_POST_DefaultDomainGroupExists_Expect_Changed() {
		String testedGroup = "group1";
		String uri = String.format("%s?%s=%s", rdGroupUri, UriVariable.GROUP.getName(), testedGroup);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource("node1", null));
		rdResource.add(new EndpointResource("node2", null));
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);

		GroupResource groupResource = new GroupResource(testedGroup, null);
		groupResource.add(new EndpointResource("node1", null));
		rdGroupResource.add(groupResource);
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(LinkFormat.serializeTree(rdResource), MediaTypeRegistry.TEXT_PLAIN)
				.getCode();

		Assert.assertEquals(ResponseCode.CHANGED, responseCode);
		ArrayList<Resource> groups = new ArrayList<>(rdGroupResource.getChildren());
		Assert.assertEquals(1, groups.size());
		Resource group = groups.get(0);
		Assert.assertEquals(testedGroup, group.getName());
		Assert.assertEquals(2, group.getChildren().size());
	}

	@Test
	public void When_DELETE_NoGroupParameter_Expect_BadRequest() {
		server.add(new RdGroupResource(new RdResource()));

		CoapClient client = new CoapClient(rdGroupUri).useExecutor();
		ResponseCode responseCode = client.delete().getCode();

		Assert.assertEquals(ResponseCode.BAD_REQUEST, responseCode);
	}

	@Test
	public void When_DELETE_GroupInWrongDomain_Expect_NotFound() {
		String testedGroup = "group1";
		String uri = String.format("%s?%s=%s&%s=domain1", rdGroupUri, UriVariable.GROUP.getName(), testedGroup,
				LinkFormat.DOMAIN);

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		rdGroupResource.add(new GroupResource(testedGroup, "domain2"));
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.delete().getCode();

		Assert.assertEquals(ResponseCode.NOT_FOUND, responseCode);
	}

	@Test
	public void When_DELETE_GroupInGoodDomain_Expect_Deleted() {
		String testedGroup = "group1";
		String testedDomain = "domain1";
		String uri = String.format("%s?%s=%s&%s=%s", rdGroupUri, UriVariable.GROUP.getName(), testedGroup,
				LinkFormat.DOMAIN, testedDomain);

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		rdGroupResource.add(new GroupResource(testedGroup, testedDomain));
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.delete().getCode();

		Assert.assertEquals(ResponseCode.DELETED, responseCode);
		Assert.assertEquals(0, rdGroupResource.getChildren().size());
	}

	@Test
	public void When_DELETE_DefaultDomain_Expect_Deleted() {
		String testedGroup = "group1";
		String uri = String.format("%s?%s=%s", rdGroupUri, UriVariable.GROUP.getName(), testedGroup);

		RdResource rdResource = new RdResource();
		RdGroupResource rdGroupResource = new RdGroupResource(rdResource);
		rdGroupResource.add(new GroupResource(testedGroup, null));
		server.add(rdGroupResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.delete().getCode();

		Assert.assertEquals(ResponseCode.DELETED, responseCode);
		Assert.assertEquals(0, rdGroupResource.getChildren().size());
	}
}
