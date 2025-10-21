package com.example.demo_redis.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "redis")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RedisProperties {

    @NotNull
    private String hostName;

    @NotNull
    private int port;

    @NotNull
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private int databaseIndex;

    private String pgtiouPrefix;

    private int pgtiouExpiryInSeconds = 120;



    @PostConstruct
    public void setupAndDebug() {




        if(pgtiouExpiryInSeconds <= 0){
            pgtiouExpiryInSeconds = 10;
            log.warn("pgtiouExpiryInSeconds value too low, defaulted to 10");
        }
        if(databaseIndex < 0){
            databaseIndex = 0;
            log.warn("Negative database index provided, defaulted to 0");
        }
        log.info("RedisProperties {}", this);
    }

    @Override
    public String toString() {
        return "RedisProperties{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", databaseIndex=" + databaseIndex +
                ", pgtiouPrefix='" + pgtiouPrefix + '\'' +
                ", pgtiouExpiryInSeconds=" + pgtiouExpiryInSeconds +
                '}';
    }
}
