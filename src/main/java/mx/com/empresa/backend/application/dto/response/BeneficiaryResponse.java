package mx.com.empresa.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mx.com.empresa.backend.domain.model.BeneficiaryStatus;

@Getter
@AllArgsConstructor
public class BeneficiaryResponse {

    private Long id;
    private String customerId;
    private String alias;
    private String holderName;
    private String clabe;
    private String bankCode;
    private BigDecimal maxAmount;
    private String email;
    private BeneficiaryStatus status;
    private LocalDateTime createdAt;

}
