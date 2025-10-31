package fr.recia.widget.api.api.rest.mail.services.imap.selector;

import fr.recia.widget.api.api.rest.mail.config.bean.ImapProperties;
import fr.recia.widget.api.api.rest.mail.config.bean.RedisProperties;
import fr.recia.widget.api.api.rest.mail.pojo.ParamUserEtabResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static fr.recia.widget.api.api.rest.mail.web.rest.api.EmailController.getCurrentHttpRequest;

@Slf4j
@Service
public class ImapSelectorServiceFromUaiImpl implements IImapSelectorService {

    @Autowired
    ImapProperties imapProperties;

    @Autowired
    RedisProperties redisProperties;

    @Autowired
    CacheManager cacheManager;

    @Override
    public String getIampHostName(String uai) throws JsonProcessingException {
        Cache cache = cacheManager.getCache(redisProperties.getUaiToImapCacheName());
        if (cache == null){
            throw new IllegalStateException(String.format("Can't get cache %s", redisProperties.getUaiToImapCacheName()));
        }
        try {
            Optional<String> hostnameFromCache = Optional.ofNullable(cache.get(uai, String.class));
            if (hostnameFromCache.isPresent()) {
                log.debug("Return hostname from cache");
                return hostnameFromCache.get();
            }
        } catch (Exception e) {
            log.error("Could not load cache for key {}", uai, e);
            cache.evict(uai);
        }

        String value = getStringResponseFromParamUserEtab(uai);
        ParamUserEtabResponse paramUserEtabResponse = getDtoFromJson(value);
        String[] escoDomaines = getEscoDomainesFromDto(paramUserEtabResponse);
        String result = getIampHostnameFromEscoDomaines(escoDomaines);

        try {
            cache.put(uai, result);
        } catch (Exception e) {
            log.error("Could not write cache for key {}, evicting key from cache, response will be returned", uai, e);
            cache.evict(uai);
        }
        return result;
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
