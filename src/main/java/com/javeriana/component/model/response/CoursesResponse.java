package com.javeriana.component.model.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CoursesResponse {
    @NotBlank
    private String idCourse;
    private String name;
    private String Area;
    private Boolean sync;
}
