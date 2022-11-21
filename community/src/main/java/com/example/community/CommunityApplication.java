package com.example.community;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.Charset;

@Slf4j
@SpringBootApplication//声明他是启动类
@EnableTransactionManagement//开启事务注解
@EnableCaching // 开启缓存，不开启数据将无法存入redis
//@ServletComponentScan//扫描拦截器
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
        System.out.println(Charset.defaultCharset());//查看当前jdk编码
        log.info("卧槽");
    }

}
