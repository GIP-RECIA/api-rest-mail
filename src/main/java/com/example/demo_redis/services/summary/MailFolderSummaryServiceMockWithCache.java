package com.example.demo_redis.services.summary;

import com.example.demo_redis.config.bean.RedisProperties;
import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.dto.MessageSummaryForWidget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.security.Principal;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
public class MailFolderSummaryServiceMockWithCache extends AbstractMailFolderSummaryWithCache {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisProperties redisProperties;

    @Override
    protected MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(Principal principal) {
        return new MailFolderSummaryForWidget(List.of(getMockMessageWithRandomValues()),UUID.randomUUID().toString(), new Random().nextInt());
    }

    private MessageSummaryForWidget getMockMessageWithRandomValues() {
        return new MessageSummaryForWidget(UUID.randomUUID().toString(), new Random().nextLong(), false);
    }

}
