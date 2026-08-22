package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "email must not be blank")
        @Size(max = 254, message = "email must not exceed 254 characters")
        String email,

        @NotBlank(message = "password must not be blank")
        String password
) {
}
