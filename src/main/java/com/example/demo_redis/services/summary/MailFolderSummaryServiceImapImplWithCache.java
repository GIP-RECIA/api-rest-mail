package com.example.demo_redis.services.summary;

import com.example.demo_redis.config.bean.AppConfProperties;
import com.example.demo_redis.config.bean.ImapProperties;
import com.example.demo_redis.config.bean.RedisProperties;
import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.dto.MessageSummaryForWidget;
import com.sun.mail.imap.IMAPFolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class MailFolderSummaryServiceImapImplWithCache extends AbstractMailFolderSummaryWithCache {

    @Autowired
    ImapProperties imapProperties;

    @Autowired
    RedisProperties redisProperties;

    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    CacheManager cacheManager;

    @Override
    protected MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(Principal principal) throws MessagingException {
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
            log.error("Could not load cache for {}", principal, e);
            cache.evict(cacheKey);
        }

        MailFolderSummaryForWidget result = getFromImap(principal);

        try {
            cache.put(cacheKey, result);
        } catch (Exception e) {
            log.error("Could not write cache for {}, evicting key from cache, response will be returned", principal, e);
            cache.evict(cacheKey);
        }

        return result;
    }


    private MailFolderSummaryForWidget getFromImap(Principal principal) throws MessagingException {


        final CasAuthenticationToken token = (CasAuthenticationToken) principal;

        log.info("CasAuthenticationToken is {}", token);

        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(appConfProperties.getCasProxyTicketFor());
        log.info("Proxy ticket is {}", proxyTicket);

        String principalUsername = token.getAssertion().getPrincipal().toString();

        IMAPFolder folder = getFolder(principalUsername, proxyTicket);
        folder.open(Folder.READ_ONLY);

        int totalMessageCount = folder.getMessageCount();
        int start = Math.max(1, totalMessageCount - (imapProperties.getMessageCountToRetrieve() - 1));
        int end = Math.max(totalMessageCount, 1);

        Message[] messages = totalMessageCount != 0
                ? folder.getMessages(start, end)
                : new Message[0];

        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.ENVELOPE);
        profile.add(FetchProfile.Item.FLAGS);
        profile.add(UIDFolder.FetchProfileItem.UID);
        folder.fetch(messages, profile);
        List<MessageSummaryForWidget> messageSummaryForWidgetList = new ArrayList<>(messages.length);

        for(Message message : messages){
            messageSummaryForWidgetList.add(new MessageSummaryForWidget(message.getSubject(), folder.getUID(message),message.getFlags().contains(Flags.Flag.SEEN)));
        }

        Collections.reverse(messageSummaryForWidgetList);

        return new MailFolderSummaryForWidget(messageSummaryForWidgetList, imapProperties.getFolderName(), folder.getUnreadMessageCount());
    }

    private IMAPFolder getFolder(String principal, String proxyTicket) throws MessagingException {

        Session session = Session.getDefaultInstance(new Properties( ));
        Store store =  session.getStore(imapProperties.getProtocol());
        store.connect(imapProperties.getHostname(), imapProperties.getPort(), principal, proxyTicket);

        return (IMAPFolder) store.getFolder(imapProperties.getFolderName());
    }
}
