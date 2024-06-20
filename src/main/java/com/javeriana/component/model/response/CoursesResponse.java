package com.javeriana.component.model.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CoursesResponse {
    @NotBlank
    private String idCourse;
    private String name;
    private String Area;
    private Boolean sync;
}
