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
package fr.recia.widget.api.apiRestMail.web.rest.api;

import fr.recia.widget.api.apiRestMail.dto.MailFolderSummaryForWidget;
import fr.recia.widget.api.apiRestMail.services.summary.IMailFolderSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    IMailFolderSummaryService mailFolderSummaryService;


    public static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }
        return null;
    }

    @GetMapping("/summary")
    public ResponseEntity<MailFolderSummaryForWidget> getEmailSummary() {
        // NOTE: The CasAuthenticationToken can also be obtained using
        // SecurityContextHolder.getContext().getAuthentication()
        log.info("SecurityContextHolder.getContext().getAuthentication() is {}", SecurityContextHolder.getContext().getAuthentication());
        try {
             return ResponseEntity.ok(mailFolderSummaryService.getMailFolderSummaryForWidget());
        } catch (Exception e) {
            log.error("An exception was thrown when trying to get mails", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
