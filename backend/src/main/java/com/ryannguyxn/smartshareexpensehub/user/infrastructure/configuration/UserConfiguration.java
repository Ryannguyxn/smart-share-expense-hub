package com.ryannguyxn.smartshareexpensehub.user.infrastructure.configuration;

import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserService;
import com.ryannguyxn.smartshareexpensehub.user.application.authenticate.AuthenticateUserUseCase;
import com.ryannguyxn.smartshareexpensehub.user.application.port.AccessTokenGenerator;
import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordVerifier;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserService;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserUseCase;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import com.ryannguyxn.smartshareexpensehub.user.infrastructure.security.BCryptPasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.infrastructure.security.BCryptPasswordVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class UserConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    PasswordHashGenerator passwordHashGenerator(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordHashGenerator(passwordEncoder);
    }

    @Bean
    PasswordVerifier passwordVerifier(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordVerifier(passwordEncoder);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHashGenerator passwordHashGenerator,
            Clock clock
    ) {
        return new RegisterUserService(userRepository, passwordHashGenerator, clock);
    }

    @Bean
    AuthenticateUserUseCase authenticateUserUseCase(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            AccessTokenGenerator accessTokenGenerator
    ) {
        return new AuthenticateUserService(
                userRepository,
                passwordVerifier,
                accessTokenGenerator
        );
    }
}
