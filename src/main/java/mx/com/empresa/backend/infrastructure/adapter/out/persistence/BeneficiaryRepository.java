package mx.com.empresa.backend.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import mx.com.empresa.backend.domain.model.BeneficiaryStatus;

@ApplicationScoped
public class BeneficiaryRepository {

	@Inject
	EntityManager entityManager;

	public Optional<BeneficiaryEntity> findById(Long id) {

		return Optional.ofNullable(entityManager.find(BeneficiaryEntity.class, id));
	}

	public List<BeneficiaryEntity> findActiveByCustomerId(String customerId) {

		return entityManager.createQuery("""
				SELECT b
				FROM BeneficiaryEntity b
				WHERE b.customerId = :customerId
				  AND b.status = :status
				""", BeneficiaryEntity.class).setParameter("customerId", customerId)
				.setParameter("status", BeneficiaryStatus.ACTIVE).getResultList();
	}

	public boolean existsByCustomerIdAndClabe(String customerId, String clabe) {

		Long count = entityManager.createQuery("""
				SELECT COUNT(b)
				FROM BeneficiaryEntity b
				WHERE b.customerId = :customerId
				  AND b.clabe = :clabe
				""", Long.class).setParameter("customerId", customerId).setParameter("clabe", clabe).getSingleResult();

		return count > 0;
	}

	public long countActiveByCustomerId(String customerId) {

		return entityManager.createQuery("""
				SELECT COUNT(b)
				FROM BeneficiaryEntity b
				WHERE b.customerId = :customerId
				  AND b.status = :status
				""", Long.class).setParameter("customerId", customerId).setParameter("status", BeneficiaryStatus.ACTIVE)
				.getSingleResult();
	}

	@Transactional
	public BeneficiaryEntity save(BeneficiaryEntity entity) {

		if (entity.getId() == null) {

			entityManager.persist(entity);
			return entity;

		} else {

			return entityManager.merge(entity);
		}
	}

}
