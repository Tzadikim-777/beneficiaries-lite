package mx.com.empresa.backend.application.service;

import java.util.List;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import mx.com.empresa.backend.application.dto.request.CreateBeneficiaryRequest;
import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;
import mx.com.empresa.backend.application.mapper.BeneficiaryResponseMapper;
import mx.com.empresa.backend.application.port.in.CreateBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.DeactivateBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.GetBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.ListBeneficiariesUseCase;
import mx.com.empresa.backend.application.port.out.BeneficiaryRepositoryPort;
import mx.com.empresa.backend.domain.exception.BeneficiaryLimitExceededException;
import mx.com.empresa.backend.domain.exception.BeneficiaryNotFoundException;
import mx.com.empresa.backend.domain.exception.DuplicateClabeException;
import mx.com.empresa.backend.domain.model.Beneficiary;

@ApplicationScoped
public class BeneficiaryService implements CreateBeneficiaryUseCase, GetBeneficiaryUseCase, ListBeneficiariesUseCase,
		DeactivateBeneficiaryUseCase {

	private static final Logger LOG =
	        Logger.getLogger(BeneficiaryService.class);
	
	private final BeneficiaryRepositoryPort repository;
	private final BeneficiaryResponseMapper responseMapper;

	@Inject
	public BeneficiaryService(
	        BeneficiaryRepositoryPort repository,
	        BeneficiaryResponseMapper responseMapper) {

	    this.repository = repository;
	    this.responseMapper = responseMapper;
	}


@Override
@Transactional
public BeneficiaryResponse create(CreateBeneficiaryRequest request) {

    Beneficiary beneficiary = new Beneficiary(
            request.getCustomerId(),
            request.getAlias(),
            request.getHolderName(),
            request.getClabe(),
            request.getMaxAmount(),
            request.getEmail()
    );

    boolean exists = repository.existsByCustomerIdAndClabe(
            beneficiary.getCustomerId(),
            beneficiary.getClabe()
    );

    if (exists) {
        throw new DuplicateClabeException();
    }

    long activeCount = repository.countActiveByCustomerId(
            beneficiary.getCustomerId()
    );

    if (activeCount >= 10) {
        throw new BeneficiaryLimitExceededException();
    }

    Beneficiary saved = repository.save(beneficiary);

    return responseMapper.toResponse(saved);
}


	@Override
	public BeneficiaryResponse getById(Long id) {
		LOG.debugf("Consultando beneficiario con ID: %d", id);

		Beneficiary beneficiary = repository.findById(id)
				.orElseThrow(() -> new BeneficiaryNotFoundException(id));

		LOG.debugf("Beneficiario encontrado. ID: %d", beneficiary.getId());
		return new BeneficiaryResponse(beneficiary.getId(), beneficiary.getCustomerId(), beneficiary.getAlias(),
				beneficiary.getHolderName(), beneficiary.getClabe(), beneficiary.getBankCode(),
				beneficiary.getMaxAmount(), beneficiary.getEmail(), beneficiary.getStatus(),
				beneficiary.getCreatedAt());
	}

	@Override
	public List<BeneficiaryResponse> listActiveByCustomerId(String customerId) {
		LOG.debug("Consultando beneficiarios activos");

		List<Beneficiary> beneficiaries = repository.findActiveByCustomerId(customerId);
		LOG.debugf(
		        "Consulta completada. Beneficiarios activos encontrados: %d",
		        beneficiaries.size()
		);

		return beneficiaries.stream()
				.map(beneficiary -> new BeneficiaryResponse(beneficiary.getId(), beneficiary.getCustomerId(),
						beneficiary.getAlias(), beneficiary.getHolderName(), beneficiary.getClabe(),
						beneficiary.getBankCode(), beneficiary.getMaxAmount(), beneficiary.getEmail(),
						beneficiary.getStatus(), beneficiary.getCreatedAt()))
				.toList();
	}

	@Override
	@Transactional
	public void deactivate(Long id) {
		LOG.infof("Iniciando desactivación de beneficiario. ID: %d", id);

		Beneficiary beneficiary = repository.findById(id)
				.orElseThrow(() -> new BeneficiaryNotFoundException(id));

		beneficiary.deactivate();

		repository.save(beneficiary);
		
		LOG.infof("Beneficiario desactivado correctamente. ID: %d", id);
	}

}
