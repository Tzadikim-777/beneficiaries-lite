
package mx.com.empresa.backend.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import mx.com.empresa.backend.domain.model.BeneficiaryStatus;
import mx.com.empresa.backend.infrastructure.adapter.out.persistence.BeneficiaryEntity;

@Path("/test/duplicate-db")
public class DuplicateClabeTestResource {

    @Inject
    EntityManager entityManager;

    @POST
    @Transactional
    public Response createDuplicate() {

        BeneficiaryEntity first = buildEntity("Proveedor A");

        entityManager.persist(first);
        entityManager.flush();

        BeneficiaryEntity second = buildEntity("Proveedor B");

        entityManager.persist(second);

        entityManager.flush();

        return Response.status(201).build();
    }

    private BeneficiaryEntity buildEntity(String alias) {

        BeneficiaryEntity entity = new BeneficiaryEntity();

        entity.setCustomerId("22223333");
        entity.setAlias(alias);
        entity.setHolderName("Proveedor de pruebas");
        entity.setClabe("032180000118359719");
        entity.setBankCode("032");
        entity.setMaxAmount(new BigDecimal("5000.00"));
        entity.setEmail("pruebas@ejemplo.com");
        entity.setStatus(BeneficiaryStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }
}
