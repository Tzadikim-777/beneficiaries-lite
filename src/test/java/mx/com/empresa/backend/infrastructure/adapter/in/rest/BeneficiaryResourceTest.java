package mx.com.empresa.backend.infrastructure.adapter.in.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import mx.com.empresa.backend.domain.model.BeneficiaryStatus;
import mx.com.empresa.backend.infrastructure.adapter.out.persistence.BeneficiaryEntity;

@QuarkusTest
class BeneficiaryResourceTest {


@AfterEach
void cleanUp() {

    QuarkusTransaction.requiringNew().run(() -> {

        entityManager.createQuery(
                """
                DELETE FROM BeneficiaryEntity b
                WHERE b.customerId IN :customerIds
                """
        )
        .setParameter(
                "customerIds",
                List.of("12345678", "88887777")
        )
        .executeUpdate();
    });
}

	
	@Test
	void shouldCreateBeneficiarySuccessfully() {

		String request = """
				{
				  "customerId": "12345678",
				  "alias": "Proveedor papelería",
				  "holderName": "Papelería La Estrella SA de CV",
				  "clabe": "032180000118359719",
				  "maxAmount": 50000.00,
				  "email": "pagos@laestrella.mx"
				}
				""";

		given().contentType("application/json").body(request).when().post("/api/v1/beneficiaries").then()
				.statusCode(201).body("id", notNullValue()).body("customerId", equalTo("12345678"))
				.body("bankCode", equalTo("032")).body("status", equalTo("ACTIVE"));
	}

	@Test
	void shouldReturn400WhenRequestIsInvalid() {

		String request = """
				{
				  "customerId": "123",
				  "alias": "AB",
				  "holderName": "",
				  "clabe": "123456",
				  "maxAmount": 0,
				  "email": "correo-invalido"
				}
				""";

		given().contentType("application/json").body(request).when().post("/api/v1/beneficiaries").then()
				.statusCode(400).body("code", equalTo("BEN-400")).body("message", equalTo("Error de validación"))
				.body("details.size()", equalTo(6))
				.body("details", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("customerId:")));
	}

	@Test
	void shouldReturn404WhenBeneficiaryDoesNotExist() {

		Long id = 99999L;

		given().pathParam("id", id).when().get("/api/v1/beneficiaries/{id}").then().statusCode(404)
				.body("code", equalTo("BEN-404")).body("message", equalTo("No existe un beneficiario con ID: 99999"))
				.body("details.size()", equalTo(0)).body("timestamp", notNullValue());
	}

	@Inject
	EntityManager entityManager;

	void insertTenBeneficiaries() {

		QuarkusTransaction.requiringNew().run(() -> {

			for (int i = 0; i < 10; i++) {

				BeneficiaryEntity entity = new BeneficiaryEntity();

				entity.setCustomerId("88887777");
				entity.setAlias("Proveedor " + i);
				entity.setHolderName("Empresa de pruebas");
				entity.setClabe(String.format("%018d", i));
				entity.setBankCode("000");
				entity.setMaxAmount(new BigDecimal("1000.00"));
				entity.setEmail("pruebas@ejemplo.com");
				entity.setStatus(BeneficiaryStatus.ACTIVE);
				entity.setCreatedAt(LocalDateTime.now());

				entityManager.persist(entity);
			}

			entityManager.flush();
		});
	}

	@Test
	void shouldReturn422WhenCustomerHasTenActiveBeneficiaries() {

		insertTenBeneficiaries();

		String request = """
				{
				  "customerId": "88887777",
				  "alias": "Proveedor once",
				  "holderName": "Empresa de pruebas",
				  "clabe": "032180000118359719",
				  "maxAmount": 5000.00,
				  "email": "proveedor@ejemplo.com"
				}
				""";

		given().contentType("application/json").body(request).when().post("/api/v1/beneficiaries").then()
				.statusCode(422).body("code", equalTo("BEN-422"))
				.body("message", equalTo("El cliente ha alcanzado el límite de 10 beneficiarios activos"));
	}

}
