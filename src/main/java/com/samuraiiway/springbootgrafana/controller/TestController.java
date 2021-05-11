package com.samuraiiway.springbootgrafana.controller;

import com.samuraiiway.springbootgrafana.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test/success")
    public ResponseEntity testSuccess() throws Exception {
        testService.test(false);
        return ResponseEntity.ok("test");
    }

    @GetMapping("/test/exception")
    public ResponseEntity testException() throws Exception {
        testService.test(true);
        return ResponseEntity.ok("test");
    }
}
