package com.javeriana.component.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CoursesSaveRequest {
    @NotBlank
    private String courseId;
    @NotBlank
    private String name;
    @NotBlank
    private String parent;
    @NotBlank
    private String description;
    @NotBlank
    private CategorySaveRequest categorySaveRequest;
}
