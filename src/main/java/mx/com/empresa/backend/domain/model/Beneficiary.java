package mx.com.empresa.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mx.com.empresa.backend.domain.exception.InvalidClabeException;
import mx.com.empresa.backend.domain.validation.ClabeValidator;

import lombok.Getter;
import mx.com.empresa.backend.domain.exception.BeneficiaryAlreadyInactiveException;

@Getter
public class Beneficiary {

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

	public Beneficiary(String customerId, String alias, String holderName, String clabe, BigDecimal maxAmount,
			String email) {

		if (!new ClabeValidator().isValid(clabe)) {
		    throw new InvalidClabeException();
		}
		this.customerId = customerId;
		this.alias = alias;
		this.holderName = holderName;
		this.clabe = clabe;
		this.maxAmount = maxAmount;
		this.email = email;

		this.bankCode = clabe.substring(0, 3);
		this.status = BeneficiaryStatus.ACTIVE;
		this.createdAt = LocalDateTime.now();
	}

	public Beneficiary(Long id, String customerId, String alias, String holderName, String clabe, String bankCode,
			BigDecimal maxAmount, String email, BeneficiaryStatus status, LocalDateTime createdAt) {

		this.id = id;
		this.customerId = customerId;
		this.alias = alias;
		this.holderName = holderName;
		this.clabe = clabe;
		this.bankCode = bankCode;
		this.maxAmount = maxAmount;
		this.email = email;
		this.status = status;
		this.createdAt = createdAt;
	}

	public void deactivate() {

		if (this.status == BeneficiaryStatus.INACTIVE) {
			throw new BeneficiaryAlreadyInactiveException();
		}

		this.status = BeneficiaryStatus.INACTIVE;
	}

}