package mx.com.empresa.backend.application.port.in;

import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;

public interface GetBeneficiaryUseCase {

    BeneficiaryResponse getById(Long id);

}
