package com.javeriana.component.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoursesRequest {
    private String name;
    private String area;
    private String courseId;
    private List<GradesRequest> gradesRequests;

}
