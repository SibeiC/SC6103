package com.chencraft.ntu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration for HTTP request logging.
 * Uses CommonsRequestLoggingFilter to log details about incoming requests,
 * excluding sensitive headers.
 */
@Configuration
public class RequestLoggingConfig {
    /**
     * Set of headers that should be skipped during logging for security or privacy.
     */
    private static final Set<String> SKIP_LOGGING_HEADERS = Stream.of(
            "x-client-cert", "x-client-dn", "X-Client-Cert", "X-Client-DN"
    ).collect(Collectors.toSet());

    /**
     * Configures a bean for logging incoming HTTP requests.
     * Includes query string, payload, and selected headers.
     *
     * @return the configured CommonsRequestLoggingFilter
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setMaxPayloadLength(10000);
        filter.setHeaderPredicate(header -> !SKIP_LOGGING_HEADERS.contains(header));
        return filter;
    }
}
