package com.example.demo_redis.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CorsProperties {

    private List<String> allowedOrigins;

    private boolean allowCredentials;

    private boolean enable;

    @Override
    public String toString() {
        return "CorsProperties{" +
                "allowedOrigins=" + allowedOrigins +
                ", allowCredentials=" + allowCredentials +
                ", enable=" + enable +
                '}';
    }

    @PostConstruct
    public void setupAndDebug() {
        log.debug(this.toString());
    }
}
