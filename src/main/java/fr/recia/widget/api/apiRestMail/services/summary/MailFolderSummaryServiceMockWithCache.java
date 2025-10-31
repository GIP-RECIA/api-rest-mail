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
package fr.recia.widget.api.apiRestMail.services.summary;

import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import fr.recia.widget.api.apiRestMail.dto.MessageSummaryForWidget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
public class MailFolderSummaryServiceMockWithCache extends AbstractMailFolderSummaryWithCache {

    @Override
    protected MailFolderSummaryForWidget getMailFolderSummaryForWidgetWithoutCache(CasAuthenticationToken token) {
        return new MailFolderSummaryForWidget(List.of(getMockMessageWithRandomValues()),UUID.randomUUID().toString(), new Random().nextInt());
    }

    private MessageSummaryForWidget getMockMessageWithRandomValues() {
        return new MessageSummaryForWidget(UUID.randomUUID().toString(), new Random().nextLong(), false);
    }
}
