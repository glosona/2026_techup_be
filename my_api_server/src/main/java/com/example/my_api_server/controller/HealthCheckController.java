package com.example.my_api_server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/health")
public class HealthCheckController { // ALB와 헬스체크하는 엔드포인트

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("연결 성공1");
    }

}