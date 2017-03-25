package de.thk.rdw.admin.data;

import java.util.List;

import de.thk.rdw.admin.model.CoapConnection;

public interface CoapConnectionDao {

	void beginTransaction();

	void commitTransaction();

	void create(CoapConnection coapConnection);

	List<CoapConnection> findAll();

	void update(CoapConnection coapConnection);

	void delete(CoapConnection coapConnection);
}
