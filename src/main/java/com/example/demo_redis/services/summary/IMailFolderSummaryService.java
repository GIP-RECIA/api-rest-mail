package com.example.demo_redis.services.summary;


import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.Principal;

@Service
public interface IMailFolderSummaryService {

    MailFolderSummaryForWidget getMailFolderSummaryForWidget(Principal principal) throws MessagingException;
}
