package org.esco.portlet.mediacentre;

import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.security.SoffitApiAuthenticationManager;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${security-configuration.soffit.jwt.signatureKey:Changeme}")
    private String signatureKey;

    @Value("${security-configuration.cors.enable}")
    private Boolean corsEnable;

    @Value("${security-configuration.cors.allow-credentials}")
    private Boolean corsAllowCredentials;

    @Value("${security-configuration.cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    @Value("${security-configuration.cors.exposed-headers}")
    private List<String> corsExposedHeaders;

    @Value("${security-configuration.cors.allowed-headers}")
    private List<String> corsAllowedHeaders;

    @Value("${security-configuration.cors.allowed-methods}")
    private List<String> corsAllowedMethods;

    @Override
    public void configure(WebSecurity web) {
        final RequestMatcher pathMatcher = new AntPathRequestMatcher("/api/**");
        final RequestMatcher inverseMatcher = new NegatedRequestMatcher(pathMatcher);
        web.ignoring().requestMatchers(inverseMatcher);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(log.isDebugEnabled()) log.debug("configure signatureKey = {}", this.signatureKey);
        final AbstractPreAuthenticatedProcessingFilter filter =
                new SoffitApiPreAuthenticatedProcessingFilter(this.signatureKey);

        filter.setAuthenticationManager(authenticationManager());

        http
                .addFilter(filter)
                .authorizeRequests()
                    .antMatchers("/health-check").anonymous()
                    .antMatchers(HttpMethod.GET, "/api/**").authenticated()
                    .anyRequest().denyAll()
                .and()
                    .cors()
                    .configurationSource(corsConfigurationSource())
                .and()
                    .sessionManagement()
                        .sessionFixation()
                            .none();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> disableSpringBootErrorFilter() {
        /*
         * The ErrorPageFilter (Spring) makes extra calls to HttpServletResponse.flushBuffer(),
         * and this behavior produces many warnings in the portal logs during portlet requests.
         */
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(errorPageFilter());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (this.corsEnable) {
            if(log.isWarnEnabled()) log.warn("CORS ABILITATI! CORS est autorisé");

            final CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowCredentials(this.corsAllowCredentials);
            configuration.setAllowedOrigins(this.corsAllowedOrigins);
            configuration.setExposedHeaders(this.corsExposedHeaders);
            configuration.setAllowedHeaders(this.corsAllowedHeaders);
            configuration.setAllowedMethods(this.corsAllowedMethods);

            source.registerCorsConfiguration("/**", configuration);
        }

        return source;
    }
}
