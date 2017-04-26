package de.thk.rdw.rd.uri;

import org.junit.Assert;
import org.junit.Test;

public class UriVariableTest {

	@Test
	public void When_NameExists_ReturnVariable() {
		UriVariable uriVariable = UriVariable.END_POINT;
		String name = uriVariable.getName();
		Assert.assertEquals(uriVariable, UriVariable.getByName(name));
	}

	@Test
	public void When_NameDontExists_ReturnNull() {
		Assert.assertNull(UriVariable.getByName("abc"));
	}

	// ENDPOINT

	@Test
	public void When_EndpointExact_Then_NoException() {
		UriVariable.END_POINT.validate(createString(63));
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_EndpointTooLong_Then_Exception() {
		UriVariable.END_POINT.validate(createString(64));
	}

	// DOMAIN

	@Test
	public void When_DomainExact_Then_NoException() {
		UriVariable.DOMAIN.validate(createString(63));
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_DomainTooLong_Then_Exception() {
		UriVariable.DOMAIN.validate(createString(64));
	}

	// ENDPOINT TYPE

	@Test
	public void When_EndpointTypeExact_Then_NoException() {
		UriVariable.END_POINT_TYPE.validate(createString(63));
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_EndpointTypeTooLong_Then_Exception() {
		UriVariable.END_POINT_TYPE.validate(createString(64));
	}

	// LIFETIME
	@Test
	public void When_LifetimeExactSmall_Then_NoException() {
		UriVariable.LIFE_TIME.validate("60");
		Assert.assertEquals(true, true);
	}

	@Test
	public void When_LifetimeExactLarge_Then_NoException() {
		UriVariable.LIFE_TIME.validate("4294967295");
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_LifetimeNotNumeric_Then_Exception() {
		UriVariable.LIFE_TIME.validate("abc");
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_LifetimeTooLarge_Then_Exception() {
		UriVariable.LIFE_TIME.validate("4294967296");
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_LifetimeTooSmall_Then_Exception() {
		UriVariable.LIFE_TIME.validate("59");
	}

	// GROUP

	@Test
	public void When_GroupExact_Then_NoException() {
		UriVariable.GROUP.validate(createString(63));
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_GroupTooLong_Then_Exception() {
		UriVariable.GROUP.validate(createString(64));
	}

	// PAGE

	@Test
	public void When_PagePositive_Then_NoException() {
		UriVariable.PAGE.validate("1");
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_PageNegative_Then_Exception() {
		UriVariable.PAGE.validate("-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_PageNotNumeric_Then_Exception() {
		UriVariable.PAGE.validate("abc");
	}

	// COUNT

	@Test
	public void When_CountPositive_Then_NoException() {
		UriVariable.COUNT.validate("1");
		Assert.assertEquals(true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_CountNegative_Then_Exception() {
		UriVariable.COUNT.validate("-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void When_CountNotNumeric_Then_Exception() {
		UriVariable.COUNT.validate("abc");
	}

	private String createString(int bytes) {
		StringBuffer outputBuffer = new StringBuffer(bytes);
		for (int i = 0; i < bytes; i++) {
			outputBuffer.append("e");
		}
		return outputBuffer.toString();
	}
}
