/**
 * Copyright © ${project.inceptionYear} GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.widget.api.apiRestMail.config;

import fr.recia.widget.api.apiRestMail.config.bean.AppConfProperties;
import fr.recia.widget.api.apiRestMail.config.custom.impl.CasSuccessHandler;
import fr.recia.widget.api.apiRestMail.config.custom.impl.CustomSessionMappingStorage;
import fr.recia.widget.api.apiRestMail.config.custom.impl.UserCustomImplementation;
import fr.recia.widget.api.apiRestMail.services.ProxyGrantingTicketRedisImpl;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public  class SecurityConfig {

    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private CustomSessionMappingStorage ticketSessionMappingStorage;

    @Autowired
    private CasSuccessHandler casSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authenticationProvider(casAuthenticationProvider(serviceProperties()))
                .addFilterBefore(casAuthenticationFilter(authenticationManager(casAuthenticationProvider(serviceProperties()))), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(casAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/health-check").permitAll()
                        .antMatchers("/api/email/summary").authenticated()
                        .antMatchers(appConfProperties.getCasTicketCallback()).permitAll()
                        .antMatchers(appConfProperties.getCasProxyReceptorUrl()).permitAll()
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
        filter.setAuthenticationSuccessHandler(casSuccessHandler);
        return filter;
    }

    /**
     * Filtre CAS pour le Single Logout (SLO).
     */
    @Bean
    public Filter singleSignOutFilter() {
        SingleSignOutFilter delegate = new SingleSignOutFilter();
        delegate.setIgnoreInitConfiguration(true);
        delegate.setArtifactParameterName("ticket");
        delegate.setLogoutParameterName("logoutRequest");

        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String logoutRequest = request.getParameter("logoutRequest");
                String ip = request.getRemoteAddr();
                String uri = request.getRequestURI();
                String method = request.getMethod();

                log.debug("[SLO] Requête entrante : {} {} depuis IP={}", method, uri, ip);

                if (logoutRequest != null) {
                    log.trace("[SLO] URI appelée : {}", uri);
                    log.trace("[SLO] Adresse IP appelante : {}", ip);
                    log.trace("[SLO] XML logoutRequest brut :\n{}", logoutRequest);

                    // Parsing XML SAML pour extraire le ticket (SessionIndex)
                    try {
                        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                        var builder = factory.newDocumentBuilder();
                        var doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(logoutRequest)));
                        doc.getDocumentElement().normalize();

                        var nameIdNode = doc.getElementsByTagName("saml:NameID").item(0);
                        var sessionIndexNode = doc.getElementsByTagName("samlp:SessionIndex").item(0);

                        String nameId = nameIdNode != null ? nameIdNode.getTextContent() : "inconnu";
                        String ticket = sessionIndexNode != null ? sessionIndexNode.getTextContent() : "inconnu";

                        // Lors du logout, le CAS envoie aussi des messages pour invalider les PGT, mais ici on ne traite que les
                        // SessionTicket, qui commencent par ST

                        int index = ticket.indexOf('-');
                        boolean isSessionTicket = false;

                        if (index != -1) {
                            String beforeDash = ticket.substring(0, index + 1);
                            if("ST-".equals(beforeDash)){
                                isSessionTicket = true;
                            }
                        }

                        if(isSessionTicket){
                            log.debug("[SLO] Ticket Invalidation Request will be handled: {}", ticket);
                        }else {
                            log.debug("[SLO] Ticket Invalidation Request will be ignored: {}", ticket);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        String sessionId = ticketSessionMappingStorage.getSessionIdFromSessionTicket(ticket);

                        log.debug("[SLO] Utilisateur CAS (NameID) : {}", nameId);
                        log.debug("[SLO] Session id: {}", sessionId);

                        ticketSessionMappingStorage.removeSessionTicket(ticket);
                        log.debug("[SLO] Le cache associé au mappage ticket-sessionID [{}:{}] a été supprimé avec succès.", ticket, sessionId);
                        ticketSessionMappingStorage.deleteSessionContext(sessionId);
                        log.debug("[SLO] Invalidation réussie de la session [{}].", sessionId);

                    } catch (Exception e) {
                        log.error("[SLO] Erreur de parsing XML logoutRequest", e);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}