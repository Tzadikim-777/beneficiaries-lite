package mx.com.empresa.backend.infrastructure.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import mx.com.empresa.backend.domain.model.Beneficiary;

@ApplicationScoped
public class BeneficiaryPersistenceMapper {

    public BeneficiaryEntity toEntity(Beneficiary beneficiary) {

        BeneficiaryEntity entity = new BeneficiaryEntity();

        entity.setId(beneficiary.getId());
        entity.setCustomerId(beneficiary.getCustomerId());
        entity.setAlias(beneficiary.getAlias());
        entity.setHolderName(beneficiary.getHolderName());
        entity.setClabe(beneficiary.getClabe());
        entity.setBankCode(beneficiary.getBankCode());
        entity.setMaxAmount(beneficiary.getMaxAmount());
        entity.setEmail(beneficiary.getEmail());
        entity.setStatus(beneficiary.getStatus());
        entity.setCreatedAt(beneficiary.getCreatedAt());

        return entity;
    }

    public Beneficiary toDomain(BeneficiaryEntity entity) {

        return new Beneficiary(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAlias(),
                entity.getHolderName(),
                entity.getClabe(),
                entity.getBankCode(),
                entity.getMaxAmount(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
