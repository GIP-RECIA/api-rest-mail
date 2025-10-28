package com.example.demo_redis.config;

import com.example.demo_redis.config.bean.AppConfProperties;
import com.example.demo_redis.config.custom.impl.UserCustomImplementation;
import com.example.demo_redis.services.ProxyGrantingTicketRedisImpl;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

@Configuration
public  class SecurityConfig {

    @Autowired
    AppConfProperties appConfProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authenticationProvider(casAuthenticationProvider(serviceProperties()))
                .addFilter(casAuthenticationFilter(authenticationManager(casAuthenticationProvider(serviceProperties()))))
                .exceptionHandling(e -> e.authenticationEntryPoint(casAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/test/redis").permitAll()
                        .antMatchers("/test/session").permitAll()
                        .antMatchers("/test/**").authenticated()
                        .antMatchers(HttpMethod.GET, appConfProperties.getCasTicketCallback()).permitAll()
                        .antMatchers(HttpMethod.GET, appConfProperties.getCasProxyReceptorUrl()).permitAll()
                        .anyRequest().denyAll()
                );
        return http.build();
    }

    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(this.appConfProperties.getCasServerLoginUrl()); //old concatenation
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }


    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(appConfProperties.getCasServiceId());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
	public ProxyGrantingTicketStorage pgtStorage(){
		return new ProxyGrantingTicketRedisImpl();
	}

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return (CasAssertionAuthenticationToken token) -> {
            Assertion assertion = token.getAssertion();
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            String username = assertion.getPrincipal().getName();
            return new UserCustomImplementation(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes);
        };
    }
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties serviceProperties) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);

        Cas20ProxyTicketValidator validator = new Cas20ProxyTicketValidator(appConfProperties.getCasServerUrl());
        validator.setProxyCallbackUrl(appConfProperties.getCasProxyTicketCallback());
        validator.setProxyGrantingTicketStorage(pgtStorage());

        provider.setTicketValidator(validator);
        provider.setAuthenticationUserDetailsService(customUserDetailsService());
        provider.setKey(appConfProperties.getCasProviderKey());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        return new ProviderManager(casAuthenticationProvider);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl(appConfProperties.getCasTicketCallback());
        filter.setProxyGrantingTicketStorage(pgtStorage());
        filter.setProxyReceptorUrl(appConfProperties.getCasProxyReceptorUrl());
        return filter;
    }
}