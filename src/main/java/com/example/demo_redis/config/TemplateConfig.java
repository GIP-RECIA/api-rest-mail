package com.example.demo_redis.config;

import com.example.demo_redis.config.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class TemplateConfig {


    @Autowired
    private RedisProperties redisProperties;
//
//    public class PrefixedStringRedisSerializer implements RedisSerializer<String> {
//        private final String prefix;
//        private final Charset charset = StandardCharsets.UTF_8;
//        private final RedisSerializer<String> delegate = new StringRedisSerializer();
//
//        public PrefixedStringRedisSerializer(String prefix) {
//            this.prefix = prefix;
//        }
//
//        @Override
//        public byte[] serialize(String s) throws SerializationException {
//            if (s == null) {
//                return null;
//            }
//            log.warn("key serialized,{}", prefix+s);
//            return delegate.serialize(prefix + s);
//        }
//
//        @Override
//        public String deserialize(byte[] bytes) throws SerializationException {
//            String key = delegate.deserialize(bytes);
//            if (key != null && key.startsWith(prefix)) {
//                return key.substring(prefix.length());
//            }
//            return key;
//        }
//    }
//
//    @Bean("redisOperations")
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        log.info("create custom redisOperation");
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        String prefix = redisProperties.getSessionKeyNamespace();
//
//        RedisSerializer<String> keySerializer = new PrefixedStringRedisSerializer(prefix);
//
//        template.setKeySerializer(keySerializer);
//        template.setHashKeySerializer(keySerializer);
//
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        template.afterPropertiesSet();
//        return template;
//    }




}
