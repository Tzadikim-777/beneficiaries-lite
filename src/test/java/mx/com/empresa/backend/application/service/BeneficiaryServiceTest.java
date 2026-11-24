package mx.com.empresa.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mx.com.empresa.backend.domain.exception.BeneficiaryNotFoundException;
import mx.com.empresa.backend.domain.exception.BeneficiaryAlreadyInactiveException;

import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.com.empresa.backend.application.dto.request.CreateBeneficiaryRequest;
import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;
import mx.com.empresa.backend.application.mapper.BeneficiaryResponseMapper;
import mx.com.empresa.backend.application.port.out.BeneficiaryRepositoryPort;
import mx.com.empresa.backend.domain.exception.BeneficiaryLimitExceededException;
import mx.com.empresa.backend.domain.exception.DuplicateClabeException;
import mx.com.empresa.backend.domain.model.Beneficiary;
import mx.com.empresa.backend.domain.model.BeneficiaryStatus;
import mx.com.empresa.backend.domain.validation.ClabeValidator;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

	@Mock
	private BeneficiaryRepositoryPort repository;

	private ClabeValidator clabeValidator;

	private BeneficiaryService service;

	@BeforeEach
	void setUp() {

		BeneficiaryResponseMapper responseMapper = new BeneficiaryResponseMapper();

		service = new BeneficiaryService(repository, responseMapper);
	}

	@Test
	void shouldRejectWhenCustomerHasTenActiveBeneficiaries() {

		CreateBeneficiaryRequest request = new CreateBeneficiaryRequest();

		request.setCustomerId("12345678");
		request.setAlias("Proveedor prueba");
		request.setHolderName("Proveedor SA de CV");
		request.setClabe("032180000118359719");
		request.setMaxAmount(new BigDecimal("5000.00"));
		request.setEmail("proveedor@ejemplo.com");

		when(repository.existsByCustomerIdAndClabe(anyString(), anyString())).thenReturn(false);

		when(repository.countActiveByCustomerId("12345678")).thenReturn(10L);

		assertThrows(BeneficiaryLimitExceededException.class, () -> service.create(request));
	}

	@Test
	void shouldRejectDuplicateClabe() {

		CreateBeneficiaryRequest request = new CreateBeneficiaryRequest();

		request.setCustomerId("12345678");
		request.setAlias("Proveedor prueba");
		request.setHolderName("Proveedor SA de CV");
		request.setClabe("032180000118359719");
		request.setMaxAmount(new BigDecimal("5000.00"));
		request.setEmail("proveedor@ejemplo.com");

		when(repository.existsByCustomerIdAndClabe("12345678", "032180000118359719")).thenReturn(true);

		assertThrows(DuplicateClabeException.class, () -> service.create(request));

		verify(repository, never()).save(any(Beneficiary.class));
	}

	@Test
	void shouldCreateBeneficiarySuccessfully() {

		CreateBeneficiaryRequest request = new CreateBeneficiaryRequest();

		request.setCustomerId("12345678");
		request.setAlias("Proveedor prueba");
		request.setHolderName("Proveedor SA de CV");
		request.setClabe("032180000118359719");
		request.setMaxAmount(new BigDecimal("5000.00"));
		request.setEmail("proveedor@ejemplo.com");

		when(repository.countActiveByCustomerId("12345678")).thenReturn(0L);

		when(repository.save(any(Beneficiary.class))).thenAnswer(invocation -> {

			Beneficiary beneficiary = invocation.getArgument(0);

			return new Beneficiary(1L, beneficiary.getCustomerId(), beneficiary.getAlias(), beneficiary.getHolderName(),
					beneficiary.getClabe(), beneficiary.getBankCode(), beneficiary.getMaxAmount(),
					beneficiary.getEmail(), beneficiary.getStatus(), beneficiary.getCreatedAt());
		});

		BeneficiaryResponse response = service.create(request);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("12345678", response.getCustomerId());
		assertEquals("Proveedor prueba", response.getAlias());
		assertEquals("032", response.getBankCode());
		assertEquals(BeneficiaryStatus.ACTIVE, response.getStatus());
		assertEquals(new BigDecimal("5000.00"), response.getMaxAmount());
		assertNotNull(response.getCreatedAt());

		ArgumentCaptor<Beneficiary> captor = ArgumentCaptor.forClass(Beneficiary.class);

		verify(repository).save(captor.capture());

		Beneficiary savedArgument = captor.getValue();

		assertEquals("032180000118359719", savedArgument.getClabe());
		assertEquals(BeneficiaryStatus.ACTIVE, savedArgument.getStatus());
	}

	@Test
	void getById_shouldReturnBeneficiary_whenExists() {

		// Arrange
		Long id = 1L;

		Beneficiary beneficiary = new Beneficiary(id, "12345678", "Proveedor", "Juan Perez", "032180000118359719",
				"032", new BigDecimal("15000.00"), "juan@example.com", BeneficiaryStatus.ACTIVE, LocalDateTime.now());

		when(repository.findById(id)).thenReturn(Optional.of(beneficiary));

		// Act
		BeneficiaryResponse response = service.getById(id);

		// Assert
		assertNotNull(response);
		assertEquals(id, response.getId());
		assertEquals("12345678", response.getCustomerId());
		assertEquals("Proveedor", response.getAlias());

		verify(repository).findById(id);
	}

	@Test
	void listActiveByCustomerId_shouldReturnActiveBeneficiaries() {

		// Arrange
		String customerId = "12345678";

		Beneficiary beneficiary = new Beneficiary(1L, customerId, "Proveedor", "Juan Perez", "032180000118359719",
				"032", new BigDecimal("15000.00"), "juan@example.com", BeneficiaryStatus.ACTIVE, LocalDateTime.now());

		when(repository.findActiveByCustomerId(customerId)).thenReturn(List.of(beneficiary));

		// Act
		List<BeneficiaryResponse> responses = service.listActiveByCustomerId(customerId);

		// Assert
		assertEquals(1, responses.size());
		assertEquals(1L, responses.get(0).getId());
		assertEquals(customerId, responses.get(0).getCustomerId());
		assertEquals(BeneficiaryStatus.ACTIVE, responses.get(0).getStatus());

		verify(repository).findActiveByCustomerId(customerId);
	}

	@Test
	void listActiveByCustomerId_shouldReturnEmptyList() {

		// Arrange
		String customerId = "87654321";

		when(repository.findActiveByCustomerId(customerId)).thenReturn(List.of());

		// Act
		List<BeneficiaryResponse> responses = service.listActiveByCustomerId(customerId);

		// Assert
		assertNotNull(responses);
		assertTrue(responses.isEmpty());

		verify(repository).findActiveByCustomerId(customerId);
	}
	

@Test
void deactivate_shouldChangeStatusAndSave() {

    // Arrange
    Long id = 1L;

    Beneficiary beneficiary = new Beneficiary(
            id,
            "12345678",
            "Proveedor",
            "Juan Perez",
            "032180000118359719",
            "032",
            new BigDecimal("15000.00"),
            "juan@example.com",
            BeneficiaryStatus.ACTIVE,
            LocalDateTime.now()
    );

    when(repository.findById(id))
            .thenReturn(Optional.of(beneficiary));

    // Act
    service.deactivate(id);

    // Assert
    assertEquals(BeneficiaryStatus.INACTIVE,
            beneficiary.getStatus());

    verify(repository).findById(id);
    verify(repository).save(beneficiary);
}

@Test
void deactivate_shouldThrowNotFound_whenBeneficiaryDoesNotExist() {

    // Arrange
    Long id = 999L;

    when(repository.findById(id))
            .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
            BeneficiaryNotFoundException.class,
            () -> service.deactivate(id)
    );

    verify(repository).findById(id);
    verify(repository, never()).save(any());
}

@Test
void deactivate_shouldThrowConflict_whenAlreadyInactive() {

    // Arrange
    Long id = 1L;

    Beneficiary beneficiary = new Beneficiary(
            id,
            "12345678",
            "Proveedor",
            "Juan Perez",
            "032180000118359719",
            "032",
            new BigDecimal("15000.00"),
            "juan@example.com",
            BeneficiaryStatus.INACTIVE,
            LocalDateTime.now()
    );

    when(repository.findById(id))
            .thenReturn(Optional.of(beneficiary));

    // Act & Assert
    assertThrows(
            BeneficiaryAlreadyInactiveException.class,
            () -> service.deactivate(id)
    );

    verify(repository).findById(id);
    verify(repository, never()).save(any());
}


}
