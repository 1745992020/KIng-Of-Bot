package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
//原因是因为 SpringBoot 在测试时不会主动启动服务器，这时 WebSocket 就会出错。
//只需要在测试类 注意是测试类中 添加注解
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 即可
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
