package fr.recia.widget.api.apiRestMail.services.summary;

import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import fr.recia.widget.api.apiRestMail.dto.MessageSummaryForWidget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
public class MailFolderSummaryServiceMockWithCache extends AbstractMailFolderSummaryWithCache {

    @Override
    protected MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(CasAuthenticationToken token) {
        return new MailFolderSummaryForWidget(List.of(getMockMessageWithRandomValues()),UUID.randomUUID().toString(), new Random().nextInt());
    }

    private MessageSummaryForWidget getMockMessageWithRandomValues() {
        return new MessageSummaryForWidget(UUID.randomUUID().toString(), new Random().nextLong(), false);
    }
}
