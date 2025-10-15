package com.example.demo_redis.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/redis")
    public ResponseEntity<String> getOk(){
        return ResponseEntity.ok("Redis url OK "+ Instant.now());
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

}
