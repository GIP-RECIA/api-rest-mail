package fr.recia.widget.api.api.rest.mail.config.custom.impl;

import fr.recia.widget.api.api.rest.mail.config.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomSessionMappingStorage {

    @Autowired
    RedisProperties redisProperties;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    protected String prefixedKey(String key){
        return String.format("%1$s:%2$s",redisProperties.getMappingPrefix(),key);
    }

    public void setSessionTicketSessionIdPair(String sessionTicket, String sessionId) {
        log.trace("[CustomSessionMappingStorage] setSessionTicketSessionIdPair {} {}", sessionTicket, sessionId);
        redisTemplate.opsForValue().set(prefixedKey(sessionTicket), sessionId);
    }

    public String getSessionIdFromSessionTicket(String sessionTicket) {
        log.trace("[CustomSessionMappingStorage] getSessionIdFromSessionTicket {}", sessionTicket);
        return redisTemplate.opsForValue().get(prefixedKey(sessionTicket));
    }

    public void removeSessionTicket(String sessionTicket) {
        log.trace("[CustomSessionMappingStorage] removeSessionTicket {}", sessionTicket);
        redisTemplate.delete(prefixedKey(sessionTicket));
    }

    public void deleteSessionContext(String sessionId) {
        log.trace("[CustomSessionMappingStorage] deleteSessionContext {}", sessionId);
        sessionRepository.deleteById(sessionId);
    }
}
