package mx.com.empresa.backend.application.port.in;

import java.util.List;

import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;

public interface ListBeneficiariesUseCase {

    List<BeneficiaryResponse> listActiveByCustomerId(String customerId);

}
