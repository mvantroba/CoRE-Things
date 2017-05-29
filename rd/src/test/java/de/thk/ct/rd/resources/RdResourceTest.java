package de.thk.ct.rd.resources;

import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RdResourceTest {

	private CoapServer server;
	private String rdUri;

	@Before
	public void setUp() throws Exception {
		CoapEndpoint endpoint = new CoapEndpoint(0);
		server = new CoapServer();
		server.addEndpoint(endpoint);
		server.start();
		// Obtain port after server has started.
		rdUri = String.format("%s://localhost:%d/%s", CoAP.COAP_URI_SCHEME, endpoint.getAddress().getPort(),
				RdResourceType.CORE_RD.getName());
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.destroy();
		}
	}

	@Test
	public void When_POST_NoParameter_Expect_BadRequest() {
		server.add(new RdResource());

		CoapClient client = new CoapClient(rdUri).useExecutor();
		ResponseCode responseCode = client.post(String.format(""), MediaTypeRegistry.TEXT_PLAIN).getCode();

		Assert.assertEquals(ResponseCode.BAD_REQUEST, responseCode);
	}

	@Test
	public void When_POST_NewEndpointWithParameters_Expect_Created() {
		String endpoint = "node1";
		String domain = "domain1";
		String endpointType = "raspberry";
		String lifetime = "60";
		String context = "coap://127.0.0.1:5683";

		String uri = String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s", //
				rdUri, //
				LinkFormat.END_POINT, endpoint, //
				LinkFormat.DOMAIN, domain, //
				LinkFormat.END_POINT_TYPE, endpointType, //
				LinkFormat.LIFE_TIME, lifetime, //
				LinkFormat.CONTEXT, context);

		RdResource rdResource = new RdResource();
		server.add(rdResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(String.format(""), MediaTypeRegistry.TEXT_PLAIN).getCode();
		EndpointResource registeredEndpoint = (EndpointResource) rdResource.getChild(endpoint);

		// Check if created.
		Assert.assertEquals(ResponseCode.CREATED, responseCode);
		Assert.assertNotNull(registeredEndpoint);

		// Check variables.
		Assert.assertEquals(endpoint, registeredEndpoint.getName());
		Assert.assertEquals(domain, registeredEndpoint.getDomain());
		Assert.assertEquals(endpointType, registeredEndpoint.getEndpointType());
		Assert.assertEquals(lifetime, String.valueOf(registeredEndpoint.getLifetime()));
		Assert.assertEquals(context, registeredEndpoint.getContext());

		// Check endpoint attribute.
		List<String> endpointAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.END_POINT);
		Assert.assertNotNull(endpointAttribute);
		Assert.assertEquals(1, endpointAttribute.size());
		Assert.assertEquals(endpoint, endpointAttribute.get(0));
		// Check domain attribute.
		List<String> domainAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.DOMAIN);
		Assert.assertNotNull(domainAttribute);
		Assert.assertEquals(1, domainAttribute.size());
		Assert.assertEquals(domain, domainAttribute.get(0));
		// Check endpoint type attribute.
		List<String> endpointTypeAttribute = registeredEndpoint.getAttributes()
				.getAttributeValues(LinkFormat.END_POINT_TYPE);
		Assert.assertNotNull(endpointTypeAttribute);
		Assert.assertEquals(1, endpointTypeAttribute.size());
		Assert.assertEquals(endpointType, endpointTypeAttribute.get(0));
		// Check lifetime attribute.
		List<String> lifetimeAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.LIFE_TIME);
		Assert.assertNotNull(lifetimeAttribute);
		Assert.assertEquals(1, lifetimeAttribute.size());
		Assert.assertEquals(lifetime, lifetimeAttribute.get(0));
		// Check context attribute.
		List<String> contextAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.CONTEXT);
		Assert.assertNotNull(contextAttribute);
		Assert.assertEquals(1, contextAttribute.size());
		Assert.assertEquals(context, contextAttribute.get(0));
	}

	@Test
	public void When_POST_UpdateEndpointWithParameters_Expect_Changed() {
		String endpoint = "node1";
		String domain = "domain1";
		String endpointType = "raspberry";
		String lifetime = "60";
		String context = "coap://127.0.0.1:5683";

		String uri = String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s", //
				rdUri, //
				LinkFormat.END_POINT, endpoint, //
				LinkFormat.DOMAIN, domain, //
				LinkFormat.END_POINT_TYPE, endpointType, //
				LinkFormat.LIFE_TIME, lifetime, //
				LinkFormat.CONTEXT, context);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource(endpoint, domain, "otherType", "420", "coap://127.0.0.1:6969"));
		server.add(rdResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(String.format(""), MediaTypeRegistry.TEXT_PLAIN).getCode();
		EndpointResource registeredEndpoint = (EndpointResource) rdResource.getChild(endpoint);

		// Check if created.
		Assert.assertEquals(ResponseCode.CHANGED, responseCode);
		Assert.assertNotNull(registeredEndpoint);

		// Check variables.
		Assert.assertEquals(endpoint, registeredEndpoint.getName());
		Assert.assertEquals(domain, registeredEndpoint.getDomain());
		Assert.assertEquals(lifetime, String.valueOf(registeredEndpoint.getLifetime()));
		Assert.assertEquals(context, registeredEndpoint.getContext());

		// Check endpoint attribute.
		List<String> endpointAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.END_POINT);
		Assert.assertNotNull(endpointAttribute);
		Assert.assertEquals(1, endpointAttribute.size());
		Assert.assertEquals(endpoint, endpointAttribute.get(0));
		// Check domain attribute.
		List<String> domainAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.DOMAIN);
		Assert.assertNotNull(domainAttribute);
		Assert.assertEquals(1, domainAttribute.size());
		Assert.assertEquals(domain, domainAttribute.get(0));
		// Check lifetime attribute.
		List<String> lifetimeAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.LIFE_TIME);
		Assert.assertNotNull(lifetimeAttribute);
		Assert.assertEquals(1, lifetimeAttribute.size());
		Assert.assertEquals(lifetime, lifetimeAttribute.get(0));
		// Check context attribute.
		List<String> contextAttribute = registeredEndpoint.getAttributes().getAttributeValues(LinkFormat.CONTEXT);
		Assert.assertNotNull(contextAttribute);
		Assert.assertEquals(1, contextAttribute.size());
		Assert.assertEquals(context, contextAttribute.get(0));
	}

	@Test
	public void When_POST_NewEndpointWithResources_Expect_Created() {
		String endpoint = "node1";
		String sensorsName = "sensors";
		String temperatureName = "temperature";
		String uri = String.format("%s?%s=%s", rdUri, LinkFormat.END_POINT, endpoint);

		// Create sensors resource, which will be used as root for serialization
		// of resources for request payload.
		CoapResource sensors = new CoapResource(sensorsName);
		sensors.add(new CoapResource(temperatureName));

		String payload = String.format("%s,%s%s", LinkFormat.serializeTree(sensors),
				LinkFormat.serializeResource(new CoapResource("servo")),
				LinkFormat.serializeResource(new CoapResource("led")));

		RdResource rdResource = new RdResource();
		server.add(rdResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(payload, MediaTypeRegistry.TEXT_PLAIN).getCode();
		EndpointResource registeredEndpoint = (EndpointResource) rdResource.getChild(endpoint);

		Assert.assertEquals(ResponseCode.CREATED, responseCode);
		Assert.assertNotNull(registeredEndpoint);
		Assert.assertEquals(3, registeredEndpoint.getChildren().size());
		Assert.assertEquals(1, registeredEndpoint.getChild(sensorsName).getChildren().size());
	}

	@Test
	public void When_POST_UpdateEndpointWithResources_Expect_Changed() {
		String endpoint = "node1";
		String sensorsName = "sensors";
		String temperatureName = "temperature";
		String uri = String.format("%s?%s=%s", rdUri, LinkFormat.END_POINT, endpoint);

		// Create sensors resource, which will be used as root for serialization
		// of resources for request payload.
		CoapResource sensors = new CoapResource(sensorsName);
		sensors.add(new CoapResource(temperatureName));

		String payload = String.format("%s,%s%s", LinkFormat.serializeTree(sensors),
				LinkFormat.serializeResource(new CoapResource("servo")),
				LinkFormat.serializeResource(new CoapResource("led")));

		RdResource rdResource = new RdResource();
		EndpointResource oldEndpointResource = new EndpointResource(endpoint, null);
		oldEndpointResource.add(new CoapResource("humidity"));
		rdResource.add(oldEndpointResource);
		server.add(rdResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.post(payload, MediaTypeRegistry.TEXT_PLAIN).getCode();
		EndpointResource registeredEndpoint = (EndpointResource) rdResource.getChild(endpoint);

		Assert.assertEquals(ResponseCode.CHANGED, responseCode);
		Assert.assertNotNull(registeredEndpoint);
		Assert.assertEquals(3, registeredEndpoint.getChildren().size());
		Assert.assertEquals(1, registeredEndpoint.getChild(sensorsName).getChildren().size());
	}

	@Test
	public void When_DELETE_DeleteExistingEndpoint_Expect_Deleted() {
		String endpoint = "node1";
		String uri = String.format("%s/%s", rdUri, endpoint);

		RdResource rdResource = new RdResource();
		rdResource.add(new EndpointResource(endpoint, null));
		server.add(rdResource);

		CoapClient client = new CoapClient(uri).useExecutor();
		ResponseCode responseCode = client.delete().getCode();

		Assert.assertEquals(ResponseCode.DELETED, responseCode);
		Assert.assertEquals(0, rdResource.getChildren().size());
	}
}
