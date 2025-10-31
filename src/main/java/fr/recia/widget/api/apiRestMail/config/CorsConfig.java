package fr.recia.widget.api.apiRestMail.config;

import fr.recia.widget.api.apiRestMail.config.bean.CorsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Autowired
    CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        if (!corsProperties.isEnable()) {
            return request -> null;
        }

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

