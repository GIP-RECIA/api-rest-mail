package com.example.demo_redis.web.rest.api;

import com.example.demo_redis.config.bean.AppConfProperties;
import com.example.demo_redis.config.bean.ImapProperties;
import com.example.demo_redis.dto.MailFolderSummaryForWidget;
import com.example.demo_redis.services.summary.IMailFolderSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ImapProperties imapProperties;

    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    IMailFolderSummaryService mailFolderSummaryService;

    @GetMapping("/redis")
    public ResponseEntity<String> getOk(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok("Redis url OK "+ Instant.now()+ " " +request.getUserPrincipal());
    }

    @GetMapping("/session")
    public Map<String, Object> sessionTest(HttpSession session) {
        Integer visits = (Integer) session.getAttribute("visits");
        if (visits == null) visits = 0;
        session.setAttribute("visits", ++visits);

        return Map.of(
                "sessionId", session.getId(),
                "visits", visits
        );
    }

    @GetMapping("/cas-secured")
    public ResponseEntity<MailFolderSummaryForWidget> getOkIfCas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {


        // NOTE: The CasAuthenticationToken can also be obtained using
        // SecurityContextHolder.getContext().getAuthentication()
        log.info("SecurityContextHolder.getContext().getAuthentication() is {}", SecurityContextHolder.getContext().getAuthentication());
        try {
             return ResponseEntity.ok(
                 mailFolderSummaryService.getMailFolderSummaryForWidget(request.getUserPrincipal())
             );

        } catch (MessagingException e) {
            log.error("MessagingException", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
