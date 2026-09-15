package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.authenticate;

import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserCommand;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserResult;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticateUserController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthenticateUserController(
            AuthenticateUserUseCase authenticateUserUseCase
    ) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping
    public ResponseEntity<AuthenticateUserResponse> authenticate(
            @Valid @RequestBody AuthenticateUserRequest request
    ) {
        AuthenticateUserResult result =
                authenticateUserUseCase.authenticate(
                        new AuthenticateUserCommand(
                                request.email(),
                                request.password()
                        )
                );
        return ResponseEntity.ok(
                new AuthenticateUserResponse(
                        result.accessToken()
                )
        );
    }
}
