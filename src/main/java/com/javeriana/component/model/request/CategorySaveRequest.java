package com.javeriana.component.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategorySaveRequest {
    @Schema(description = "Nombre del curso", example = "Course1")
    @NotBlank(message = "Campo requerido- name")
    private String name;

    @Schema(description = "Padre del curso, si no tiene dejar vacio", example = "ParentCourse1")
    private String parent;

    @Schema(description = "Descripcion del curso", example = "Course 1 example")
    @NotBlank(message = "Campo requerido - description")
    private String description;
}
