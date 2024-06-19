package com.javeriana.component.model.request;

import com.javeriana.component.model.response.Grades;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GradesRequest {
    private String studentUserName;
    private List<Grades> grades;
}
