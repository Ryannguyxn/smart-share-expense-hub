package com.ryannguyxn.smartshareexpensehub.group.infrastructure.configuration;

import com.ryannguyxn.smartshareexpensehub.group.infrastructure.persistence.jpa.GroupJpaMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GroupConfiguration {

    @Bean
    GroupJpaMapper groupJpaMapper() {
        return new GroupJpaMapper();
    }
}