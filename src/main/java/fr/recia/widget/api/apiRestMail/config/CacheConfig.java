/**
 * Copyright © 2025 GIP-RECIA (https://www.recia.fr/)
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
package fr.recia.widget.api.apiRestMail.config;

import fr.recia.widget.api.apiRestMail.config.bean.RedisProperties;
import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        ObjectMapper objectMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();

        JacksonJsonRedisSerializer<MailFolderSummaryForWidget> mailFolderSerializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        MailFolderSummaryForWidget.class
                );
        // responses cache
        cacheConfigurations.put(
                redisProperties.getResponseCacheName(),
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(mailFolderSerializer)
                        )
                        .entryTtl(Duration.ofSeconds(
                                redisProperties.getResponseCacheTtlInSeconds()
                        ))
        );
        // uai -> hostname cache
        cacheConfigurations.put(redisProperties.getUaiToImapCacheName(), RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJacksonJsonRedisSerializer.builder()
                        .enableSpringCacheNullValueSupport()
                        .build()))
                .entryTtl(Duration.ofSeconds(redisProperties.getUaiToImapCacheTtlInSeconds())));
        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}