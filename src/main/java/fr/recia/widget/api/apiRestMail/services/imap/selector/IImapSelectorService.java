package fr.recia.widget.api.apiRestMail.services.imap.selector;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IImapSelectorService {

    String getIampHostName(String uai) throws JsonProcessingException;

}
