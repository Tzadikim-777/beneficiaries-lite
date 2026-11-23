package mx.com.empresa.backend.application.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;
import mx.com.empresa.backend.domain.model.Beneficiary;

@ApplicationScoped
public class BeneficiaryResponseMapper {

    public BeneficiaryResponse toResponse(Beneficiary beneficiary) {

        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getCustomerId(),
                beneficiary.getAlias(),
                beneficiary.getHolderName(),
                beneficiary.getClabe(),
                beneficiary.getBankCode(),
                beneficiary.getMaxAmount(),
                beneficiary.getEmail(),
                beneficiary.getStatus(),
                beneficiary.getCreatedAt()
        );
    }
}
