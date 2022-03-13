package com.socialnetwork.chat.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("default-auditor")
public class DefaultAuditor implements AuditorAware<String> {

    public static final String SYSTEM_AUDITOR = "123e4567-e89b-42d3-a456-556642440000";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM_AUDITOR);
    }
}
