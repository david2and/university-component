package com.javeriana.component.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GradesResponseDTO {
    private List<GradeDTO> grades;
    private List<String> warnings;
}
