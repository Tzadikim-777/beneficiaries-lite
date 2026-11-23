package mx.com.empresa.backend.infrastructure.adapter.in.rest;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.com.empresa.backend.application.dto.request.CreateBeneficiaryRequest;
import mx.com.empresa.backend.application.dto.response.BeneficiaryResponse;
import mx.com.empresa.backend.application.port.in.CreateBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.DeactivateBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.GetBeneficiaryUseCase;
import mx.com.empresa.backend.application.port.in.ListBeneficiariesUseCase;

@Path("/api/v1/beneficiaries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Beneficiaries", description = "Administración de beneficiarios bancarios")
public class BeneficiaryResource {

	private final CreateBeneficiaryUseCase createBeneficiaryUseCase;
	private final GetBeneficiaryUseCase getBeneficiaryUseCase;
	private final ListBeneficiariesUseCase listBeneficiariesUseCase;
	private final DeactivateBeneficiaryUseCase deactivateBeneficiaryUseCase;

	@Inject
	public BeneficiaryResource(CreateBeneficiaryUseCase createBeneficiaryUseCase,
			GetBeneficiaryUseCase getBeneficiaryUseCase, ListBeneficiariesUseCase listBeneficiariesUseCase,
			DeactivateBeneficiaryUseCase deactivateBeneficiaryUseCase) {

		this.createBeneficiaryUseCase = createBeneficiaryUseCase;
		this.getBeneficiaryUseCase = getBeneficiaryUseCase;
		this.listBeneficiariesUseCase = listBeneficiariesUseCase;
		this.deactivateBeneficiaryUseCase = deactivateBeneficiaryUseCase;
	}

	@POST
	@Operation(summary = "Registrar beneficiario", description = "Registra un nuevo beneficiario bancario para un cliente")
	@APIResponse(responseCode = "201", description = "Beneficiario registrado correctamente")
	@APIResponse(responseCode = "400", description = "Datos inválidos o CLABE incorrecta")
	@APIResponse(responseCode = "409", description = "La CLABE ya está registrada para este cliente")
	@APIResponse(responseCode = "422", description = "El cliente alcanzó el límite de 10 beneficiarios activos")
	public Response create(@Valid CreateBeneficiaryRequest request) {

		BeneficiaryResponse beneficiary = createBeneficiaryUseCase.create(request);

		return Response.status(Response.Status.CREATED).entity(beneficiary).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Consultar beneficiario por ID", description = "Obtiene la información de un beneficiario "
			+ "mediante su identificador único")
	@APIResponse(responseCode = "200", description = "Beneficiario encontrado correctamente")
	@APIResponse(responseCode = "404", description = "El beneficiario no existe")
	public Response getById(@PathParam("id") Long id) {

		BeneficiaryResponse beneficiary = getBeneficiaryUseCase.getById(id);

		return Response.ok(beneficiary).build();
	}

	@GET
	@Operation(summary = "Listar beneficiarios activos", description = "Consulta todos los beneficiarios activos "
			+ "pertenecientes a un cliente")
	@APIResponse(responseCode = "200", description = "Lista de beneficiarios activos obtenida correctamente")
	@APIResponse(responseCode = "400", description = "El customerId es inválido")
	public Response listActiveByCustomerId(
			@Parameter(description = "Identificador del cliente de exactamente 8 dígitos", required = true, example = "12345678") @QueryParam("customerId") @NotBlank(message = "El customerId es obligatorio") @Pattern(regexp = "\\d{8}", message = "El customerId debe contener exactamente 8 dígitos") String customerId) {

		List<BeneficiaryResponse> beneficiaries = listBeneficiariesUseCase.listActiveByCustomerId(customerId);

		return Response.ok(beneficiaries).build();
	}

	@PATCH
	@Path("/{id}/deactivate")
	@Operation(summary = "Desactivar beneficiario", description = "Realiza la baja lógica de un beneficiario, "
			+ "cambiando su estado de ACTIVE a INACTIVE")
	@APIResponse(responseCode = "204", description = "Beneficiario desactivado correctamente")
	@APIResponse(responseCode = "404", description = "El beneficiario no existe")
	@APIResponse(responseCode = "409", description = "El beneficiario ya se encuentra inactivo")
	public Response deactivate(@PathParam("id") Long id) {

		deactivateBeneficiaryUseCase.deactivate(id);

		return Response.noContent().build();
	}

}
