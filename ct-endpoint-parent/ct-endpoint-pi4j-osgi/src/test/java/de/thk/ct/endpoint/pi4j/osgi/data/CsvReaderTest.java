package de.thk.ct.endpoint.pi4j.osgi.data;

import org.junit.Assert;
import org.junit.Test;

import de.thk.ct.endpoint.device.osgi.resources.ActuatorType;
import de.thk.ct.endpoint.device.osgi.resources.ResourceType;
import de.thk.ct.endpoint.device.osgi.resources.SensorType;

public class CsvReaderTest {

	@Test
	public void When_SensorArgumentsValid_Expect_True() {
		String[] arguments = new String[] { "name", SensorType.MERCURY_SWITCH.getType(), "1" };
		Assert.assertTrue(CsvReader.validate(arguments, ResourceType.SENSOR));
	}

	@Test
	public void When_ActautorArgumentsValid_Expect_True() {
		String[] arguments = new String[] { "name", ActuatorType.LED.getType(), "1" };
		Assert.assertTrue(CsvReader.validate(arguments, ResourceType.ACTUATOR));
	}

	@Test
	public void When_ArgumentsNull_Expect_False() {
		Assert.assertFalse(CsvReader.validate(null, ResourceType.SENSOR));
	}

	@Test
	public void When_ArgumentsSizeSmall_Expect_False() {
		String[] arguments = new String[] { "name", SensorType.MERCURY_SWITCH.getType(), };
		Assert.assertFalse(CsvReader.validate(arguments, ResourceType.SENSOR));
	}

	@Test
	public void When_NameEmpty_Expect_False() {
		String[] arguments = new String[] { " ", SensorType.MERCURY_SWITCH.getType(), "1" };
		Assert.assertFalse(CsvReader.validate(arguments, ResourceType.SENSOR));
	}

	@Test
	public void When_TypeEmpty_Expect_False() {
		String[] arguments = new String[] { "name", " ", "1" };
		Assert.assertFalse(CsvReader.validate(arguments, ResourceType.SENSOR));
	}

	@Test
	public void When_SensorNotExists_Expect_False() {
		String[] arguments = new String[] { "name", "i dont exist :(", "1" };
		Assert.assertFalse(CsvReader.validate(arguments, ResourceType.SENSOR));
	}

	@Test
	public void When_ActuatorNotExists_Expect_False() {
		String[] arguments = new String[] { "name", "me neither :'(", "1" };
		Assert.assertFalse(CsvReader.validate(arguments, ResourceType.ACTUATOR));
	}
}
