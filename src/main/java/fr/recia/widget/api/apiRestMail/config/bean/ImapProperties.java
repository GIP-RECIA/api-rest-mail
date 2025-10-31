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
package fr.recia.widget.api.apiRestMail.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "imap")
@Data
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ImapProperties {

    @NotNull
    private String hostname;

    private int port;

    private boolean debug;

    private int connectionTimeout;

    private int timeout;

    private int messageCountToRetrieve;

    private String protocol;

    private Map<String, String> escoDomainesConversion = new HashMap<>();

    private String folderName;

    private String uriParamUserEtabGetEtabFromUai;

    @Override
    public String toString() {
        return "ImapProperties{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                ", debug=" + debug +
                ", connectionTimeout=" + connectionTimeout +
                ", timeout=" + timeout +
                ", messageCountToRetrieve=" + messageCountToRetrieve +
                ", protocol='" + protocol + '\'' +
                ", escoDomainesConversion=" + escoDomainesConversion +
                ", folderName='" + folderName + '\'' +
                ", uriParamUserEtabGetEtabFromUai='" + uriParamUserEtabGetEtabFromUai + '\'' +
                '}';
    }

    @PostConstruct
    public void setupAndDebug() {
        log.debug(this.toString());
    }

}
