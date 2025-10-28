package com.example.demo_redis.services.imap.selector;

import com.example.demo_redis.config.bean.ImapProperties;
import com.example.demo_redis.pojo.ParamUserEtabResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.example.demo_redis.web.rest.api.TestController.getCurrentHttpRequest;

@Slf4j
@Service
public class ImapSelectorServiceFromUaiImpl implements IImapSelectorService {

    @Autowired
    ImapProperties imapProperties;

    @Override
    public String getIampHostName(String uai) throws JsonProcessingException {

        String value =getStringResponseFromParamUserEtab(uai);

        return getIampHostnameFromEscoDomaines(getEscoDomainesFromDto(getDtoFromJson(value)));
    }


    String getStringResponseFromParamUserEtab(String uai){

        HttpServletRequest request = getCurrentHttpRequest();

        assert request != null;

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String baseUrl = scheme + "://" + serverName +
                (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        String uri = baseUrl + imapProperties.getUriParamUserEtabGetEtabFromUai().replace("{code}", uai);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,String.class);
        return response.getBody();
    }


    String getIampHostnameFromEscoDomaines(String[] escoDomaines){
        //Filter to keep only escoDomaine value equals to a key in the conversion map
        String[] filteredEscoDomaines = (String[]) Arrays.stream(escoDomaines)
                .filter(x -> imapProperties.getEscoDomainesConversion().containsKey(x)).toArray(String[]::new);



       if(filteredEscoDomaines.length == 0){
           //No match with convertion map -> use default imap
           return imapProperties.getHostname();
       }else {
           //Get imap hostname using esco domaine as key
           return imapProperties.getEscoDomainesConversion().get(filteredEscoDomaines[0]);
       }
    }


    ParamUserEtabResponse getDtoFromJson(String jsonValue) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonValue, ParamUserEtabResponse.class);
    }

    String[] getEscoDomainesFromDto(ParamUserEtabResponse paramUserEtabResponse) {
        return paramUserEtabResponse.getOtherAttributes().getESCODomaines();
    }

}
