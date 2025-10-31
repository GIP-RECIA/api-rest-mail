package fr.recia.widget.api.apiRestMail.config.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "app.conf")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AppConfProperties {

    @NotNull
    private String casServiceId;

    @NotNull
    private String casServerUrl;

    @NotNull
    private String casServerLoginUrl;

    @NotNull
    private String casProviderKey;

    @NotNull
    private String casTicketCallback;

    @NotNull
    private  String casProxyReceptorUrl;

    @NotNull
    private  String casProxyTicketCallback;

    @NotNull
    private String casProxyTicketFor;

    @NotNull
    private String casAttributesKeyCurrentEtab;

    @PostConstruct
    public void setupAndDebug() {
        log.debug("AppConfProperties {}", this);
    }

}