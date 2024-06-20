package com.javeriana.component.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AdminRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
