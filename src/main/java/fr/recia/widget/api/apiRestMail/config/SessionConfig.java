package fr.recia.widget.api.apiRestMail.config;

import fr.recia.widget.api.apiRestMail.config.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
