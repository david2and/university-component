package com.javeriana.component.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MoodleGradesResponseDTO {
    private List<GradeDTO> grades;
}
