package fr.recia.widget.api.api.rest.mail.services.summary;


import fr.recia.widget.api.api.rest.mail.dto.MailFolderSummaryForWidget;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface IMailFolderSummaryService {

    MailFolderSummaryForWidget getMailFolderSummaryForWidget() throws MessagingException, JsonProcessingException;
}
