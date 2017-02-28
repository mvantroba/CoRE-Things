package de.thk.rdw.rd.uri;

/**
 * Resource directory entries can be exported to other discovery mechanisms such
 * as DNS-SD. For that reason, parameters should be limited to a maximum length
 * of 63 Bytes.
 *
 */
public interface UriVariableValidator {

	void validate(String value);
}
