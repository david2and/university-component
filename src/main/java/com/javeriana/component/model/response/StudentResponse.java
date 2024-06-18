package com.javeriana.component.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentResponse {
    private String username;
    private List<Grades> grades;
}
