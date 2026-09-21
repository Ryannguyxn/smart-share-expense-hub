package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public final class SystemRoleJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String roleClaim = jwt.getClaimAsString("systemRole");

        UserSystemRole systemRole;

        try {
            systemRole = UserSystemRole.valueOf(roleClaim);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token"),
                    "Invalid systemRole claim"
            );
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + systemRole.name())
        );

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                jwt.getSubject()
        );
    }
}