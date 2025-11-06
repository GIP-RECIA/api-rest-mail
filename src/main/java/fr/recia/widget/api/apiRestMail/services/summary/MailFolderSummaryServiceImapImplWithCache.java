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

import fr.recia.widget.api.apiRestMail.config.bean.AppConfProperties;
import fr.recia.widget.api.apiRestMail.config.bean.ImapProperties;
import fr.recia.widget.api.apiRestMail.config.custom.impl.UserCustomImplementation;
import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import fr.recia.widget.api.apiRestMail.dto.MessageSummaryForWidget;
import fr.recia.widget.api.apiRestMail.exceptions.NoCurrentUAIException;
import fr.recia.widget.api.apiRestMail.services.imap.selector.ImapSelectorServiceFromUaiImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.mail.imap.IMAPFolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
public class MailFolderSummaryServiceImapImplWithCache extends AbstractMailFolderSummaryWithCache {

    @Autowired
    ImapProperties imapProperties;

    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    ImapSelectorServiceFromUaiImpl imapSelectorServiceFromUai;

    @Override
    protected MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(CasAuthenticationToken token) throws MessagingException, JsonProcessingException {
        return getFromImap(token);
    }

    private MailFolderSummaryForWidget getFromImap(CasAuthenticationToken token) throws MessagingException, JsonProcessingException {

        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(appConfProperties.getCasProxyTicketFor());

        String principalUsername = token.getAssertion().getPrincipal().toString();

        IMAPFolder folder = getFolder(principalUsername, proxyTicket, token);
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

    private IMAPFolder getFolder(String principal, String proxyTicket, CasAuthenticationToken token) throws MessagingException, JsonProcessingException {

        UserCustomImplementation userDetails = (UserCustomImplementation) token.getUserDetails();
        if(userDetails.getAttributes().containsKey(appConfProperties.getCasAttributesKeyCurrentEtab())){
            String uai = userDetails.getAttributes().get(appConfProperties.getCasAttributesKeyCurrentEtab()).toString();
            String hostname = imapSelectorServiceFromUai.getIampHostName(uai);

            Session session = Session.getDefaultInstance(new Properties( ));
            Store store =  session.getStore(imapProperties.getProtocol());
            store.connect(hostname, imapProperties.getPort(), principal, proxyTicket);

            return (IMAPFolder) store.getFolder(imapProperties.getFolderName());
        } else {
            throw new NoCurrentUAIException("No UAI was found for "+principal);
        }
    }
}
