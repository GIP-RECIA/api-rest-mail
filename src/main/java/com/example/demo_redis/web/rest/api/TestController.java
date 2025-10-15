package com.example.demo_redis.web.rest.api;

import com.example.demo_redis.config.bean.AppConfProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    AppConfProperties appConfProperties;

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
    public ResponseEntity<String> getOkIfCas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
// NOTE: The CasAuthenticationToken can also be obtained using
// SecurityContextHolder.getContext().getAuthentication()


        final CasAuthenticationToken token = (CasAuthenticationToken) request.getUserPrincipal();
// proxyTicket could be reused to make calls to the CAS service even if the
// target url differs
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(appConfProperties.getCasProxyTicketFor());


        request.getSession(true).setAttribute("user", token.getAssertion().getPrincipal().toString());

        //  addLink(UUID.randomUUID(),Instant.now().toString());

        System.out.println(proxyTicket);
        return ResponseEntity.ok(proxyTicket + " " +  token.getAssertion().getPrincipal().toString() + " " + SecurityContextHolder.getContext().getAuthentication() + " "+request.getUserPrincipal());
    }

}
