package com.javeriana.component.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursesSaveResponse {
    private Integer moodleId;
    private String shortName;
    private CategorySaveResponse categorySaveResponse;
}
