package com.example.demo_redis.services.summary;

import com.example.demo_redis.config.bean.RedisProperties;
import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.Optional;

@Slf4j
public abstract class AbstractMailFolderSummaryWithCache implements IMailFolderSummaryService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisProperties redisProperties;

    public final MailFolderSummaryForWidget getMailFolderSummaryForWidget(Principal principal) throws MessagingException {

        Cache cache = cacheManager.getCache(redisProperties.getResponseCacheName());
        String cacheKey = principal.getName();

        if (cache == null){
            throw new IllegalStateException(String.format("Can't get cache %s", redisProperties.getResponseCacheName()));
        }

        try {
            Optional<MailFolderSummaryForWidget> summaryFromCache = Optional.ofNullable(cache.get(cacheKey, MailFolderSummaryForWidget.class));
            if (summaryFromCache.isPresent()) {
                log.debug("Return value from cache");
                return summaryFromCache.get();
            }
        } catch (Exception e) {
            log.error("Could not load cache for key {}", principal, e);
            cache.evict(cacheKey);
        }

        MailFolderSummaryForWidget result = getMailFolderSummaryForWidgetWithoutCache(principal);

        try {
            cache.put(cacheKey, result);
        } catch (Exception e) {
            log.error("Could not write cache for key {}, evicting key from cache, response will be returned", principal, e);
            cache.evict(principal);
        }

        return result;
    }

    protected abstract MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(Principal principal) throws MessagingException;

}
