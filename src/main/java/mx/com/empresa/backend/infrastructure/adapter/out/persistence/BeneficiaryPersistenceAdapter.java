package mx.com.empresa.backend.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import mx.com.empresa.backend.application.port.out.BeneficiaryRepositoryPort;
import mx.com.empresa.backend.domain.model.Beneficiary;

@ApplicationScoped
public class BeneficiaryPersistenceAdapter
        implements BeneficiaryRepositoryPort {

    private final BeneficiaryRepository repository;
    private final BeneficiaryPersistenceMapper mapper;

    @Inject
    public BeneficiaryPersistenceAdapter(
            BeneficiaryRepository repository,
            BeneficiaryPersistenceMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Beneficiary save(Beneficiary beneficiary) {

        BeneficiaryEntity entity = mapper.toEntity(beneficiary);

        BeneficiaryEntity saved = repository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Beneficiary> findById(Long id) {

        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Beneficiary> findActiveByCustomerId(
            String customerId) {

        return repository.findActiveByCustomerId(customerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCustomerIdAndClabe(
            String customerId, String clabe) {

        return repository.existsByCustomerIdAndClabe(
                customerId, clabe);
    }

    @Override
    public long countActiveByCustomerId(String customerId) {

        return repository.countActiveByCustomerId(customerId);
    }
}
