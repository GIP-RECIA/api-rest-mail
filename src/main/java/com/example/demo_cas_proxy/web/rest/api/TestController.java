package com.example.demo_cas_proxy.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/redis")
    public ResponseEntity<String> getOk(){
        return ResponseEntity.ok("Redis url OK "+ Instant.now());
    }
}
