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
package fr.recia.widget.api.apiRestMail.services.imap.selector;

import fr.recia.widget.api.apiRestMail.config.bean.ImapProperties;
import fr.recia.widget.api.apiRestMail.config.bean.RedisProperties;
import fr.recia.widget.api.apiRestMail.pojo.ParamUserEtabResponse;
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

import static fr.recia.widget.api.apiRestMail.web.rest.api.EmailController.getCurrentHttpRequest;

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
        String result = getImapHostnameFromEscoDomaines(escoDomaines);

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

        final String scheme = request.getScheme();
        final String serverName = request.getServerName();
        final int serverPort = request.getServerPort();

        String baseUrl = scheme + "://" + serverName +
                (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        String uri = (imapProperties.getUriParamUserEtabGetEtabFromUai().startsWith("/") ? baseUrl : "" ) + imapProperties.getUriParamUserEtabGetEtabFromUai().replace("{code}", uai);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,String.class);
        return response.getBody();
    }


    String getImapHostnameFromEscoDomaines(String[] escoDomaines){
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
