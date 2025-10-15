package com.example.demo_redis.config;

import com.example.demo_redis.config.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

@Slf4j
@Configuration
public class SessionConfig {

    @Autowired
    private RedisProperties redisProperties;

//        @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
//                redisProperties.getHostName(), redisProperties.getPort());
//        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabaseIndex());
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        redisStandaloneConfiguration.setUsername(redisProperties.getUserName());
//        LettuceClientConfiguration configuration = LettuceClientConfiguration.builder().build();
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, configuration);
//    }

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



//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;



//    @Autowired
//    RedisSerializer<Object> redisSerializer;

    @Bean
public RedisIndexedSessionRepository sessionRepository(RedisOperations<Object, Object> redisTemplate) {
  RedisIndexedSessionRepository sessionRepo = new RedisIndexedSessionRepository(redisTemplate);
  sessionRepo.setDefaultMaxInactiveInterval(redisProperties.getSessionExpiryInSeconds());
//  sessionRepo.setDefaultSerializer(redisSerializer);
  sessionRepo.setRedisKeyNamespace(redisProperties.getSessionKeyNamespace());
  return sessionRepo;
}

}
