package mx.com.empresa.backend.application.port.in;

import mx.com.empresa.backend.application.dto.request.CreateBeneficiaryRequest;
import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;

public interface CreateBeneficiaryUseCase {

    BeneficiaryResponse create(CreateBeneficiaryRequest request);

}