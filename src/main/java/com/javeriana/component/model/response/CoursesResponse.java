package com.javeriana.component.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursesResponse {
    private String idCourse;
    private String name;
    private String Area;
    private Boolean sync;
}
