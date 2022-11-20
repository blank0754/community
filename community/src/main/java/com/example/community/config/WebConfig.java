//package com.example.community.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * 跨域处理的配置类
// * */
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    //跨域方法
//    public void addCorsMappings(CorsRegistry registry) {
//        registry
//                .addMapping("/**")//允许访问的路径
//                .allowedOrigins("http://localhost")//配置请求来源
//                .allowedMethods("GET","POST","DELETE","PUT","OPTION")//允许跨域访问的方法
//                .allowCredentials(true)//允许携带参数
////                .allowedHeaders();
//                .maxAge(3600);//最大响应时间
//    }
//}
