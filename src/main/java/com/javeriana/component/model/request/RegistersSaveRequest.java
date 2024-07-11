package com.javeriana.component.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegistersSaveRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String courseId;
    @NotBlank
    private String roleId;
}
