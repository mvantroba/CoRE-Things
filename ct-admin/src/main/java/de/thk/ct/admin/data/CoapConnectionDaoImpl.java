package de.thk.ct.admin.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.thk.ct.admin.model.CoapConnection;

public class CoapConnectionDaoImpl implements CoapConnectionDao {

	private EntityManager entityManager;

	public CoapConnectionDaoImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void beginTransaction() {
		entityManager.getTransaction().begin();
	}

	@Override
	public void commitTransaction() {
		entityManager.getTransaction().commit();
	}

	@Override
	public void create(CoapConnection coapConnection) {
		entityManager.persist(coapConnection);
	}

	@Override
	public List<CoapConnection> findAll() {
		TypedQuery<CoapConnection> select = entityManager.createQuery(
				String.format("SELECT e FROM %s e", CoapConnection.class.getSimpleName()), CoapConnection.class);
		return select.getResultList();
	}

	@Override
	public CoapConnection findById(CoapConnection coapConnection) {
		return entityManager.find(CoapConnection.class, coapConnection.getId());
	}

	@Override
	public void update(CoapConnection coapConnection) {
		entityManager.merge(coapConnection);
	}

	@Override
	public void delete(CoapConnection coapConnection) {
		entityManager.remove(coapConnection);
	}
}
