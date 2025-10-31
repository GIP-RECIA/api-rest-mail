package com.example.demo_redis.web.rest.api;

import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.services.summary.IMailFolderSummaryService;
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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @GetMapping("/preview")
    public ResponseEntity<MailFolderSummaryForWidget> getEmailPreview()
            throws IOException {

        // NOTE: The CasAuthenticationToken can also be obtained using
        // SecurityContextHolder.getContext().getAuthentication()
        log.info("SecurityContextHolder.getContext().getAuthentication() is {}", SecurityContextHolder.getContext().getAuthentication());
        try {
             return ResponseEntity.ok(
                 mailFolderSummaryService.getMailFolderSummaryForWidget()
             );

        } catch (MessagingException e) {
            log.error("MessagingException", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
