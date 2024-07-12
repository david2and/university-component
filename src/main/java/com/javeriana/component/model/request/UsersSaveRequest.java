package com.javeriana.component.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsersSaveRequest {

    @Schema(description = "UserName del usuario", example = "juan22lop")
    @NotBlank(message = "Campo requerido - userName")
    private String userName;

    @Schema(description = "Password del usuario, debe tener 8 caracteres, una mayuscula y un caracter especial (*, - o #)", example = "Jasd123*")
    @NotBlank(message = "Campo requerido - password")
    @Size(min = 8, message = "El password debe tener al menos 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[*\\-#]).*$",
            message = "El password debe contener al menos una mayúscula y un carácter especial (*, - o #)"
    )
    private String password;

    @Schema(description = "Primer nombre del usuario", example = "Juan")
    @NotBlank(message = "Campo requerido - firstName")
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Lopez")
    @NotBlank(message = "Campo requerido - lastName")
    private String lastName;

    @Schema(description = "Email del usuario", example = "lopez@gmail.com")
    @NotBlank(message = "Campo requerido - email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Numero de ID del usuario", example = "12345856")
    @NotBlank(message = "Campo requerido - idNumber")
    private String idNumber;
}
