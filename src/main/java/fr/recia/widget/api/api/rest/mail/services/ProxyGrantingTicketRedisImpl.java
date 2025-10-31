package fr.recia.widget.api.api.rest.mail.services;

import fr.recia.widget.api.api.rest.mail.config.bean.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ProxyGrantingTicketRedisImpl implements ProxyGrantingTicketStorage {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisProperties redisProperties;

    @Override
    public void save(final String proxyGrantingTicketIou, final String proxyGrantingTicket) {
        log.debug("Saving ProxyGrantingTicketIOU and ProxyGrantingTicket combo: [{}, {}]", proxyGrantingTicketIou.substring(0,8),
                proxyGrantingTicket.substring(0,8));
        saveInRedis(proxyGrantingTicketIou, proxyGrantingTicket);
    }

    /**
     * NOTE: you can only retrieve a ProxyGrantingTicket once with this method.
     * Its removed after retrieval.
     */
    @Override
    public String retrieve(final String proxyGrantingTicketIou) {
        if (CommonUtils.isBlank(proxyGrantingTicketIou)) {
            return null;
        }

        final var proxyGrantingTicket = getFromRedis(proxyGrantingTicketIou);

        if (proxyGrantingTicket == null) {
            log.debug("No Proxy Ticket found for [{}].", proxyGrantingTicketIou);
            return null;
        }

        log.debug("Returned ProxyGrantingTicket of [{}]", proxyGrantingTicket);
        return proxyGrantingTicket;
    }

    @Override
    public void cleanUp() {
        log.warn("Redis does not require cleanup for PGT, entries have ttl in Redis");
    }

    public void saveInRedis(String Iou, String test) {
        redisTemplate.opsForValue().set(String.format("%1$s:%2$s",  redisProperties.getPgtiouPrefix(), Iou), test, redisProperties.getPgtiouExpiryInSeconds(), TimeUnit.SECONDS);
    }

    public String getFromRedis(String Iou){
        return redisTemplate.opsForValue().getAndDelete(String.format("%1$s:%2$s",  redisProperties.getPgtiouPrefix(), Iou));
    }
}


