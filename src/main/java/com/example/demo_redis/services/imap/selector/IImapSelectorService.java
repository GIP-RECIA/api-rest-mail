package com.example.demo_redis.services.imap.selector;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IImapSelectorService {

    String getIampHostName(String uai) throws JsonProcessingException;

}
