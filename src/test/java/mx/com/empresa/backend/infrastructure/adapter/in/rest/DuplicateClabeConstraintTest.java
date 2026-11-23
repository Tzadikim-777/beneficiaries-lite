package mx.com.empresa.backend.infrastructure.adapter.in.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DuplicateClabeConstraintTest {

	@Inject
	EntityManager entityManager;

	@Test
	void shouldReturn409WhenDatabaseDetectsDuplicateClabe() {

		given().when().post("/test/duplicate-db").then().statusCode(409).body("code", equalTo("BEN-409"))
				.body("message", equalTo("La CLABE ya está registrada para este cliente")).body("details", empty())
				.body("timestamp", notNullValue());
	}

	@Test
	void shouldRollbackTransactionWhenDuplicateClabeOccurs() {

		given().when().post("/test/duplicate-db").then().statusCode(409);

		Long count = entityManager.createQuery("""
				SELECT COUNT(b)
				FROM BeneficiaryEntity b
				WHERE b.customerId = :customerId
				""", Long.class).setParameter("customerId", "22223333").getSingleResult();

		assertEquals(0L, count);
	}

}
