package com.ryannguyxn.smartshareexpensehub.user.infrastructure.web.authenticate;

import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserCommand;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserResult;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserUseCase;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.InvalidCredentialsException;
import com.ryannguyxn.smartshareexpensehub.user.infrastructure.security.UserSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticateUserController.class)
@Import({
        AuthenticateUserExceptionHandler.class,
        UserSecurityConfiguration.class,
        AuthenticateUserControllerTest.TestBeansConfiguration.class
})
class AuthenticateUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticateUserUseCase authenticateUserUseCase;

    private ControllableAuthenticateUserUseCase controllableAuthenticateUserUseCase;

    @BeforeEach
    void setUp() {
        controllableAuthenticateUserUseCase =
                (ControllableAuthenticateUserUseCase) authenticateUserUseCase;
        controllableAuthenticateUserUseCase.reset();
    }

    @Test
    void shouldAuthenticateUserAndReturnAccessToken() throws Exception {
        controllableAuthenticateUserUseCase.returnAccessToken(
                "test-access-token"
        );

        mockMvc.perform(post("/api/v1/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": "password-that-must-not-be-logged"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-access-token"));

        assertEquals(
                "member@example.test",
                controllableAuthenticateUserUseCase.receivedCommand().email()
        );

        assertEquals(
                "password-that-must-not-be-logged",
                controllableAuthenticateUserUseCase.receivedCommand().rawPassword()
        );
    }

    @Test
    void shouldRejectBlankEmail() throws Exception {
        mockMvc.perform(post("/api/v1/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": " ",
                                  "password": "password-that-must-not-be-logged"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("email must not be blank"));
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        mockMvc.perform(post("/api/v1/authentication")
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
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        controllableAuthenticateUserUseCase.failWith(
                new InvalidCredentialsException()
        );

        mockMvc.perform(post("/api/v1/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "member@example.test",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication failed"))
                .andExpect(jsonPath("$.detail").value("Invalid email or password."));
    }

    @Test
    void shouldRejectAnonymousRequestToProtectedRoute() throws Exception {
        mockMvc.perform(get("/api/v1/protected-resource"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestBeansConfiguration {

        @Bean
        AuthenticateUserUseCase authenticateUserUseCase() {
            return new ControllableAuthenticateUserUseCase();
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                throw new JwtException("JWT decoding is not used in this controller test");
            };
        }
    }

    private static final class ControllableAuthenticateUserUseCase
            implements AuthenticateUserUseCase {

        private AuthenticateUserResult result;
        private RuntimeException failure;
        private AuthenticateUserCommand receivedCommand;

        void reset() {
            result = new AuthenticateUserResult("default-test-access-token");
            failure = null;
            receivedCommand = null;
        }

        void returnAccessToken(String accessToken) {
            result = new AuthenticateUserResult(accessToken);
        }

        void failWith(RuntimeException failure) {
            this.failure = failure;
        }

        AuthenticateUserCommand receivedCommand() {
            return receivedCommand;
        }

        @Override
        public AuthenticateUserResult authenticate(AuthenticateUserCommand command) {
            receivedCommand = command;

            if (failure != null) {
                throw failure;
            }

            return result;
        }
    }
}