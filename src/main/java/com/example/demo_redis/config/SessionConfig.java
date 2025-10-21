package com.example.demo_redis.config;

import com.example.demo_redis.config.bean.RedisProperties;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Slf4j
@Configuration
@EnableRedisHttpSession(redisNamespace = "${spring.redis.namespace}")
public class SessionConfig {

    @Autowired
    private RedisProperties redisProperties;


    @Bean
    // Fix exception at launch where redis would require an unnecessary permission
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("Load LettuceConnectionFactory");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHostName());
        config.setPort(redisProperties.getPort());
        config.setPassword(redisProperties.getPassword());
        config.setUsername(redisProperties.getUserName());
        config.setDatabase(redisProperties.getDatabaseIndex());
        return new LettuceConnectionFactory(config);
    }

}
