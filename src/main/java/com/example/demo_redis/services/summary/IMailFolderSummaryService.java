package com.example.demo_redis.services.summary;


import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface IMailFolderSummaryService {

    MailFolderSummaryForWidget getMailFolderSummaryForWidget() throws MessagingException, JsonProcessingException;
}
