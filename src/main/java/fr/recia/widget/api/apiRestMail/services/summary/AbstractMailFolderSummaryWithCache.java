/**
 * Copyright © ${project.inceptionYear} GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.widget.api.apiRestMail.services.summary;

import fr.recia.widget.api.apiRestMail.config.bean.RedisProperties;
import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.mail.MessagingException;
import java.util.Optional;

@Slf4j
public abstract class AbstractMailFolderSummaryWithCache implements IMailFolderSummaryService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisProperties redisProperties;

    public final MailFolderSummaryForWidget getMailFolderSummaryForWidget() throws MessagingException, JsonProcessingException {

        CasAuthenticationToken token = (CasAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        String username = token.getName();

        Cache cache = cacheManager.getCache(redisProperties.getResponseCacheName());
        String cacheKey = username;

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
            log.error("Could not load cache for key {}", cacheKey, e);
            cache.evict(cacheKey);
        }

        MailFolderSummaryForWidget result = getMailFolderSummaryForWidgetWithoutCache(token);

        try {
            cache.put(cacheKey, result);
        } catch (Exception e) {
            log.error("Could not write cache for key {}, evicting key from cache, response will be returned", cacheKey, e);
            cache.evict(cacheKey);
        }

        return result;
    }

    protected abstract MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(CasAuthenticationToken token) throws MessagingException, JsonProcessingException;

}
