package com.ryannguyxn.smartshareexpensehub.user.infrastructure.configuration;

import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserService;
import com.ryannguyxn.smartshareexpensehub.user.application.register.RegisterUserUseCase;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import com.ryannguyxn.smartshareexpensehub.user.infrastructure.security.BCryptPasswordHashGenerator;
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
}
