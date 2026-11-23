package mx.com.empresa.backend.application.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBeneficiaryRequest {

    @NotBlank(message = "El customerId es obligatorio")
    @Pattern(
        regexp = "\\d{8}",
        message = "El customerId debe contener exactamente 8 dígitos"
    )
    private String customerId;

    @NotBlank(message = "El alias es obligatorio")
    @Size(
        min = 3,
        max = 30,
        message = "El alias debe tener entre 3 y 30 caracteres"
    )
    private String alias;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(
        max = 100,
        message = "El nombre del titular no debe superar 100 caracteres"
    )
    private String holderName;

    @NotBlank(message = "La CLABE es obligatoria")
    @Pattern(
        regexp = "\\d{18}",
        message = "La CLABE debe contener exactamente 18 dígitos"
    )
    private String clabe;

    @NotNull(message = "El monto máximo es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = false,
        message = "El monto máximo debe ser mayor a cero"
    )
    @Digits(
        integer = 15,
        fraction = 2,
        message = "El monto máximo debe tener como máximo 2 decimales"
    )
    private BigDecimal maxAmount;

    @Email(message = "El correo electrónico no es válido")
    private String email;
}
