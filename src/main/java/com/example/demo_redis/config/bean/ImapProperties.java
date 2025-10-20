package com.example.demo_redis.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConfigurationProperties(prefix = "imap")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ImapProperties {

    @NotNull
    private String hostname;

    private int port;

    private boolean debug;

    private int connectionTimeout;

    private int timeout;

    private int messageCountToRetrieve;

    private String protocol;

    private String folderName;

    @Override
    public String toString() {
        return "ImapProperties{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                ", debug=" + debug +
                ", connectionTimeout=" + connectionTimeout +
                ", timeout=" + timeout +
                ", messageCountToRetrieve=" + messageCountToRetrieve +
                ", protocol='" + protocol + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }

    @PostConstruct
    public void setupAndDebug() {
        log.debug(this.toString());
    }

}
