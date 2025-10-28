package com.example.demo_redis.services.summary;

import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.dto.MessageSummaryForWidget;
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
