package de.thk.rdw.admin.usecase;

import java.util.List;

import de.thk.rdw.admin.model.CoapConnection;

public interface MainUseCase {

	List<CoapConnection> findAllConnections();

	void cleanUp();
}
