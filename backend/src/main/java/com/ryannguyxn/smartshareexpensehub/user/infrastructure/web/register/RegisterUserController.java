package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.register;


import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserCommand;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserResult;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class RegisterUserController {
    private final RegisterUserUseCase registerUserUseCase;

    public RegisterUserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        RegisterUserResult result = registerUserUseCase.register(
                new RegisterUserCommand(request.email(), request.password())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(result.userId()));
    }
}
