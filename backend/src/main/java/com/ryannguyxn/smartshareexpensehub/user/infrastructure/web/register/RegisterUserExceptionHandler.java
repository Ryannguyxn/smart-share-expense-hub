package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.register;

import com.ryannguyxn.smartshareexpensehub.user.application.register.EmailAlreadyRegisteredException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = RegisterUserController.class)
public class RegisterUserExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidationFailure(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more request fields are invalid."
        );
        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);

        return problem;

    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidEmail() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "The email address is invalid."
        );
        problem.setTitle("Validation failed");
        problem.setProperty("errors", Map.of("email", "email is invalid"));

        return problem;

    }

    @ExceptionHandler({
            EmailAlreadyRegisteredException.class,
            DataIntegrityViolationException.class
    })
    ProblemDetail handleEmailAlreadyRegistered() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "An account with this email already exists."
        );
        problem.setTitle("Email already registered");

        return problem;
    }
}
