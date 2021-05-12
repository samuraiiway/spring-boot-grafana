package com.samuraiiway.springbootgrafana.service;

import com.samuraiiway.springbootgrafana.annotation.CustomTimed;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
public class TestService {

    private static final Random random = new SecureRandom();

    @Timed("test_timed")
    public void test(boolean isException) throws Exception {
        Thread.sleep(random.nextInt(1000) * 5);

        if (isException) {
            throw new Exception("Exception");
        }
    }

    @CustomTimed("test_custom_timed")
    public String testCustomTimed(boolean isException, String name) throws Exception {
        Thread.sleep(random.nextInt(100) * 5);

        if (isException) {
            throw new Exception("Exception");
        }

        return "Hello: " + name;
    }
}
