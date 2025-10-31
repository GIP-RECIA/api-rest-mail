package fr.recia.widget.api.api.rest.mail.config;

import fr.recia.widget.api.api.rest.mail.services.summary.IMailFolderSummaryService;
import fr.recia.widget.api.api.rest.mail.services.summary.MailFolderSummaryServiceImapImplWithCache;
import fr.recia.widget.api.api.rest.mail.services.summary.MailFolderSummaryServiceMockWithCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ServiceConfig {
    @Bean(name = "mailFolderSummary")
    @ConditionalOnProperty(prefix = "mock", name = "enable", havingValue = "true")
    public IMailFolderSummaryService getServiceMock(){
        log.info("create mock bean for IMailFolderSummaryService");
        return new MailFolderSummaryServiceMockWithCache();
    }

    @Bean(name = "mailFolderSummary")
    @ConditionalOnMissingBean
    public IMailFolderSummaryService getServiceImap(){
        log.info("create imap impl bean for IMailFolderSummaryService");
        return new MailFolderSummaryServiceImapImplWithCache();
    }
}
