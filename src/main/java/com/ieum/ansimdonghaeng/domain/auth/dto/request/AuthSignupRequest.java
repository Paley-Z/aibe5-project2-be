package com.ieum.ansimdonghaeng.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthSignupRequest(

    @Email(message = "invalid email format")
    @NotBlank(message = "email is required")
    String email,

    @NotBlank(message = "password is required")
    String password,

    @NotBlank(message = "name is required")
    String name,

    String phone,

    String intro
) {
}
