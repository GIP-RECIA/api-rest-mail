package com.example.demo_redis.services.summary;

import com.example.demo_redis.config.bean.ImapProperties;
import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.dto.MessageSummaryForWidget;
import com.sun.mail.imap.IMAPFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class MailFolderSummaryServiceImapImpl implements  IMailFolderSummaryService{

    @Autowired
    ImapProperties imapProperties;

    @Override
    public MailFolderSummaryForWidget getMailFolderSummaryForWidget(String principal, String proxyTicket) throws MessagingException {

        Session session = Session.getDefaultInstance(new Properties( ));
        Store store = session.getStore(imapProperties.getProtocol());
        store.connect(imapProperties.getHostname(), imapProperties.getPort(), principal, proxyTicket);

        IMAPFolder folder = (IMAPFolder) store.getFolder(imapProperties.getFolderName());
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
}
