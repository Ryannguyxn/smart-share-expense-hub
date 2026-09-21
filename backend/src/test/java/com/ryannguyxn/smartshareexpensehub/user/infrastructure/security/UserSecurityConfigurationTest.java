package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSecurityConfigurationTest.ProtectedTestController.class)
@Import({
        UserSecurityConfiguration.class,
        UserSecurityConfigurationTest.TestBeansConfiguration.class
})
class UserSecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecoder jwtDecoder;

    private ControllableJwtDecoder controllableJwtDecoder;

    @BeforeEach
    void setUp() {
        controllableJwtDecoder = (ControllableJwtDecoder) jwtDecoder;
        controllableJwtDecoder.reset();
    }

    @Test
    void shouldRejectRequestWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/test/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestWithValidBearerToken() throws Exception {
        controllableJwtDecoder.acceptToken("valid-token");

        mockMvc.perform(get("/test/protected")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer valid-token"
                        ))
                .andExpect(status().isOk())
                .andExpect(content().string("protected-ok"));
    }

    @Test
    void shouldExposeUserIdentityAndRoleInSecurityContext() throws Exception {
        String userId = UUID.randomUUID().toString();

        controllableJwtDecoder.acceptToken("valid-token", userId);

        mockMvc.perform(get("/test/protected/identity")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer valid-token"
                        ))
                .andExpect(status().isOk())
                .andExpect(content().string(userId));
    }

    @Test
    void shouldRejectRequestWhenJwtIsInvalid() throws Exception {
        controllableJwtDecoder.rejectToken();

        mockMvc.perform(get("/test/protected")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer invalid-token"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestBeansConfiguration {

        @Bean
        JwtDecoder jwtDecoder() {
            return new ControllableJwtDecoder();
        }

        @Bean
        ProtectedTestController protectedTestController() {
            return new ProtectedTestController();
        }
    }

    static final class ControllableJwtDecoder implements JwtDecoder {

        private String acceptedToken;
        private String acceptedUserId;
        private boolean reject;

        void reset() {
            acceptedToken = null;
            acceptedUserId = null;
            reject = false;
        }

        void acceptToken(String token, String userId) {
            acceptedToken = token;
            acceptedUserId = userId;
            reject = false;
        }

        void acceptToken(String token) {
            acceptToken(token, UUID.randomUUID().toString());
        }

        void rejectToken() {
            reject = true;
        }

        @Override
        public Jwt decode(String token) {
            if (reject || !token.equals(acceptedToken)) {
                throw new BadJwtException("Invalid JWT");
            }

            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plusSeconds(300);

            return Jwt.withTokenValue(token)
                    .header("alg", "HS256")
                    .subject(acceptedUserId)
                    .issuedAt(issuedAt)
                    .expiresAt(expiresAt)
                    .claim("systemRole", "USER")
                    .build();
        }
    }

    @RestController
    static class ProtectedTestController {

        @GetMapping("/test/protected")
        String protectedResource() {
            return "protected-ok";
        }
        @GetMapping("/test/protected/identity")
        String currentIdentity() {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            boolean hasUserRole = authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_USER")
                    );

            if (!hasUserRole) {
                throw new IllegalStateException("ROLE_USER is missing");
            }

            return authentication.getName();
        }
    }
}