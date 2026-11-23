
package mx.com.empresa.backend.application.port.out;

import java.util.List;
import java.util.Optional;

import mx.com.empresa.backend.domain.model.Beneficiary;

public interface BeneficiaryRepositoryPort {

    Beneficiary save(Beneficiary beneficiary);

    Optional<Beneficiary> findById(Long id);

    List<Beneficiary> findActiveByCustomerId(String customerId);

    boolean existsByCustomerIdAndClabe(
            String customerId, String clabe);

    long countActiveByCustomerId(String customerId);

}
