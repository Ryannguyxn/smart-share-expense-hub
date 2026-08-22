package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.register;

import com.ryannguyxn.smartshareexpensehub.user.application.register.EmailAlreadyRegisteredException;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserCommand;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserResult;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserUseCase;
import com.ryannguyxn.smartshareexpensehub.user.infrastructure.security.UserSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterUserController.class)
@Import({
        RegisterUserExceptionHandler.class,
        UserSecurityConfiguration.class,
        RegisterUserControllerTest.TestBeansConfiguration.class
})
class RegisterUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    private ControllableRegisterUserUseCase controllableRegisterUserUseCase;

    @BeforeEach
    void setUp() {
        controllableRegisterUserUseCase =
                (ControllableRegisterUserUseCase) registerUserUseCase;
        controllableRegisterUserUseCase.reset();
    }

    @Test
    void shouldRegisterUserAndReturnCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        controllableRegisterUserUseCase.returnUserId(userId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": "password-that-must-not-be-logged"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        assertEquals(
                "member@example.test",
                controllableRegisterUserUseCase.receivedCommand().email()
        );
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").value("password must not be blank"));
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyRegistered() throws Exception {
        controllableRegisterUserUseCase.failWith(
                new EmailAlreadyRegisteredException("member@example.test")
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": "password-that-must-not-be-logged"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Email already registered"));
    }

    @Test
    void shouldReturnConflictWhenDatabaseRejectsConcurrentDuplicateEmail() throws Exception {
        controllableRegisterUserUseCase.failWith(
                new DataIntegrityViolationException("unique constraint violation")
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": "password-that-must-not-be-logged"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Email already registered"));
    }

    @Test
    void shouldRejectAnonymousRequestToProtectedRoute() throws Exception {
        mockMvc.perform(get("/api/v1/protected-resource"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestBeansConfiguration {

        @Bean
        RegisterUserUseCase registerUserUseCase() {
            return new ControllableRegisterUserUseCase();
        }
    }

    private static final class ControllableRegisterUserUseCase
            implements RegisterUserUseCase {

        private RegisterUserResult result;
        private RuntimeException failure;
        private RegisterUserCommand receivedCommand;

        void reset() {
            result = new RegisterUserResult(UUID.randomUUID());
            failure = null;
            receivedCommand = null;
        }

        void returnUserId(UUID userId) {
            result = new RegisterUserResult(userId);
        }

        void failWith(RuntimeException failure) {
            this.failure = failure;
        }

        RegisterUserCommand receivedCommand() {
            return receivedCommand;
        }

        @Override
        public RegisterUserResult register(RegisterUserCommand command) {
            receivedCommand = command;

            if (failure != null) {
                throw failure;
            }

            return result;
        }
    }
}
