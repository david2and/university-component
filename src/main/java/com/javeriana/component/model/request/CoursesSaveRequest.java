package com.javeriana.component.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CoursesSaveRequest {
    @Schema(description = "Id del curso por parte de la universidad", example = "123")
    @NotBlank(message = "Campo requerido - courseId")
    private String courseId;

    @Schema(description = "Nombre del curso", example = "CourseName")
    @NotBlank(message = "Campo requerido - name")
    private String name;

    @Schema(description = "Padre del curso, si no tiene dejar en blanco", example = "ParentCourse")
    private String parent;

    @Schema(description = "Nombre corto del curso", example = "ShortCrs")
    @NotBlank(message = "Campo requerido - shortName")
    private String shortName;

    private CategorySaveRequest categorySaveRequest;
}
