package com.javeriana.component.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminRequest {
    @Schema(description = "Username del usuario", example = "Juan22")
    @NotBlank(message = "Campo requerido - username")
    private String username;

    @Schema(description = "Password del usuario", example = "AJSNE123*")
    @NotBlank(message = "Campo requerido - password")
    private String password;
}
