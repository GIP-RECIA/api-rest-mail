package com.example.demo_redis.services.summary;


import com.example.demo_redis.dto.MailFolderSummaryForWidget;

import javax.mail.MessagingException;

public interface IMailFolderSummaryService {

    MailFolderSummaryForWidget getMailFolderSummaryForWidget(String principal, String proxyTicket) throws MessagingException;
}
