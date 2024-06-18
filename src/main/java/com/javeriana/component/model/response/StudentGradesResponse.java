package com.javeriana.component.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentGradesResponse {
    private List<StudentResponse> students;
}
