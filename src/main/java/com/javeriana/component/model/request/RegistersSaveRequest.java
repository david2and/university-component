package com.javeriana.component.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistersSaveRequest {
    @Schema(description = "UserName del usuario", example = "Juan22lop")
    @NotBlank(message = "Campo requerido - userName")
    private String userName;

    @Schema(description = "Id del curso de la universidad al que se quiere registrar el usuario", example = "123")
    @NotBlank(message = "Campo requerido - courseId")
    private String courseId;

    @Schema(description = "Rol del usuario, puede ser TEACHER o STUNDENT", example = "STUNDENT")
    @NotBlank(message = "Campo requerido - roleId, El rol debe ser TEACHER o STUDENT")
    @Pattern(regexp = "TEACHER|STUDENT", message = "El rol debe ser TEACHER o STUDENT")
    private String roleId;
}
