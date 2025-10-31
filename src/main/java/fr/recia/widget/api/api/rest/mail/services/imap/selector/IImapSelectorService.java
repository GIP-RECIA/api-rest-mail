package fr.recia.widget.api.api.rest.mail.services.imap.selector;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IImapSelectorService {

    String getIampHostName(String uai) throws JsonProcessingException;

}
